import { Component, OnInit, signal, computed, inject, Injectable } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { PostService } from '../../core/services/post.service';
import { User } from '../../shared/models/user.model';
import { Post } from '../../shared/models/post.model';
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    // RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatChipsModule,
    MatDialogModule
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private postService = inject(PostService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  
  user = signal<User | null>(null);
  posts = signal<Post[]>([]);
  isLoading = signal(false);
  isLoadingPosts = signal(false);
  errorMessage = signal<string | null>(null);
  
  currentUser = this.authService.currentUser;
  isOwnProfile = computed(() => {
    return this.user()?.username === this.currentUser()?.username;
  });

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const username = params['username'];
      if (username) {
        this.loadUserProfile(username);
      }
    });
  }

  loadUserProfile(username: string): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.userService.getUserByUsername(username).subscribe({
      next: (user) => {
        this.user.set(user);
        this.isLoading.set(false);
        this.loadUserPosts(user.id);
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.errorMessage.set('Failed to load profile');
        this.isLoading.set(false);
      }
    });
  }

  loadUserPosts(userId: number): void {
    this.isLoadingPosts.set(true);

    this.postService.getPostsByAuthor(userId, 0, 20).subscribe({
      next: (response) => {
        this.posts.set(response.content);
        this.isLoadingPosts.set(false);
        console.log("lcouuuuuuuuoooouuouount",response.content.length);
        
      },
      error: (error) => {
        console.error('Error loading posts:', error);
        this.isLoadingPosts.set(false);
      }
    });
  }

  toggleFollow(): void {
    const user = this.user();
    if (!user) return;

    const observable = user.isFollowing 
      ? this.userService.unfollowUser(user.id)
      : this.userService.followUser(user.id);

    observable.subscribe({
      next: () => {
        this.user.update(current => {
          if (!current) return current;
          return {
            ...current,
            isFollowing: !current.isFollowing,
            followersCount: current.isFollowing 
              ? (current.followersCount || 1) - 1 
              : (current.followersCount || 0) + 1
          };
        });
      },
      error: (error) => {
        console.error('Error toggling follow:', error);
      }
    });
  }

  editProfile(): void {
  this.router.navigate(['/profile-edit']);
}

  viewPost(postId: number): void {
    this.router.navigate(['/posts', postId]);
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  getMediaUrl(filename: string): string {
    return this.postService.getMediaUrl(filename);
  }

  getAvatarUrl(): string {
    const avatar = this.user()?.avatar;
    return avatar ? this.getMediaUrl(avatar) : '';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}