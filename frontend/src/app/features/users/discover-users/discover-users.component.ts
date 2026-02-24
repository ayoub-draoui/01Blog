import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { User } from '../../../shared/models/user.model';

@Component({
  selector: 'app-discover-users',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './discover-users.component.html',
  styleUrl: './discover-users.component.scss'
})
export class DiscoverUsersComponent implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private postService = inject(PostService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

    users = signal<User[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  currentPage = signal(0);
  hasMore = signal(true);
  isSearching = signal(false);

   searchControl = new FormControl('');

     currentUser = this.authService.currentUser;

  ngOnInit(): void {
    this.loadUsers();
    this.setupSearch();
  }
    setupSearch(): void {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged()  
      )
      .subscribe(query => {
        if (query && query.trim().length > 0) {
          this.searchUsers(query.trim());
        } else {
          this.loadUsers();
        }
      });
  }


   loadUsers(append: boolean = false): void {
    this.isLoading.set(true);
    this.isSearching.set(false);
    this.errorMessage.set(null);

    const page = append ? this.currentPage() + 1 : 0;

    this.userService.getAllUsers(page, 20).subscribe({
      next: (response) => {
        if (append) {
          this.users.update(current => [...current, ...response.content]);
        } else {
          this.users.set(response.content);
        }

        this.currentPage.set(page);
        this.hasMore.set(!response.last);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.errorMessage.set('Failed to load users');
        this.isLoading.set(false);
      }
    });
  }



  searchUsers(query: string): void {
    this.isLoading.set(true);
    this.isSearching.set(true);
    this.errorMessage.set(null);

    this.userService.searchUsers(query, 0, 20).subscribe({
      next: (response) => {
        this.users.set(response.content);
        this.currentPage.set(0);
        this.hasMore.set(!response.last);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error searching users:', error);
        this.errorMessage.set('Search failed');
        this.isLoading.set(false);
      }
    });
  }



  loadMore(): void {
    if (!this.isLoading() && this.hasMore() && !this.isSearching()) {
      this.loadUsers(true);
    }
  }


  clearSearch(): void {
    this.searchControl.setValue('');
    this.loadUsers();
  }


  toggleFollow(user: User): void {
    const observable = user.isFollowing
      ? this.userService.unfollowUser(user.id)
      : this.userService.followUser(user.id);

    observable.subscribe({
      next: () => {
        // Update user in list
        this.users.update(users =>
          users.map(u => {
            if (u.id === user.id) {
              return {
                ...u,
                isFollowing: !u.isFollowing,
                followersCount: u.isFollowing
                  ? (u.followersCount || 1) - 1
                  : (u.followersCount || 0) + 1
              };
            }
            return u;
          })
        );

        const message = user.isFollowing
          ? `Unfollowed ${user.username}`
          : `Following ${user.username}`;
        this.snackBar.open(message, 'Close', { duration: 2000 });
      },
      error: (error) => {
        console.error('Error toggling follow:', error);
        this.snackBar.open('Failed to update follow status', 'Close', { duration: 3000 });
      }
    });
  }

  viewProfile(username: string): void {
    this.router.navigate(['/profile', username]);
  }

   goBack(): void {
    this.router.navigate(['/home']);
  }

   getAvatarUrl(avatar: string | null | undefined): string {
    if (!avatar) return '';
    return this.postService.getMediaUrl(avatar);
  }



  isCurrentUser(userId: number): boolean {
    return this.currentUser()?.id === userId;
  }







}
