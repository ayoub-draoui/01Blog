import { Component, OnInit, signal, computed, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { Post } from '../../shared/models/post.model';
import { ReportDialogComponent } from '../../shared/components/reports/report-dialog.component';
import { ReportService } from '../../core/services/report.service';

import { NotificationPanelComponent } from '../../shared/components/notification-panel/notification-panel.component';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    // RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatToolbarModule,
    MatMenuModule,
    NotificationPanelComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  private postServices = inject(PostService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);
  private router = inject(Router);
  private reportService = inject(ReportService);
  private snackBar = inject(MatSnackBar);
  posts = signal<Post[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  currentPage = signal(0);
  hasMore = signal(true);
  feedType = signal<'home' | 'explore'>('home');

  currentUser = this.authService.currentUser;
  isEmpty = computed(() => this.posts().length === 0 && !this.isLoading());
  isAuthor = signal(false);
  userId = computed(() => {
    const user = this.currentUser();
    console.log('__________________________________________________________________-');
    console.log(user?.id);
    return user ? user.id : null;
  });
  isAdmin = computed(() => this.currentUser()?.role === 'ROLE_ADMIN');
  // constructor(
  //   // public authService: AuthService,
  //   private postServices: PostService,
  //   private router: Router,
  // ) {}
  switchTheme(theme: 'light' | 'dark'): void {
    document.body.classList.remove('light', 'dark');
    document.body.classList.add(theme);
  }
  ngOnInit(): void {
    console.log('Hanni fel home');
    this.loadPosts();
  }

  loadPosts(append: boolean = false): void {
    console.log('Posts____________________________________________');

    this.isLoading.set(true);
    this.errorMessage.set(null);
    const page = append ? this.currentPage() + 1 : 0;
    const feedObservable =
      this.feedType() === 'home'
        ? this.postServices.getFeed(page)
        : this.postServices.getExploreFeed(page);
    feedObservable.subscribe({
      next: (response) => {

        if (append) {
          this.posts.update((current) => [...current, ...response.content]);
        } else {
          this.posts.set(response.content);
        }
        for (const post of response.content) {
          // console.log('vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvPost ID:', post.id);
          
          console.log('vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvAuthor ID:', post.authorId);
        }
        this.currentPage.set(page);
        this.hasMore.set(!response.last);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.log('an error has occure while loading posts', error);
        this.errorMessage.set('failled to bring posts');
        this.isLoading.set(false);
      },
    });
  }
  switchFeed(type: 'home' | 'explore'): void {
    this.feedType.set(type);
    this.posts.set([]);
    this.currentPage.set(0);
    this.loadPosts();
  }
  loadMore(): void {
    if (!this.isLoading() && this.hasMore()) {
      this.loadPosts(true);
    }
  }

  sharePost(): void {
    const post = this.posts();
    if (!post) return;

    const url = window.location.href;
    navigator.clipboard.writeText(url).then(() => {
      this.snackBar.open('Link copied to clipboard!', 'Close', { duration: 2000 });
    });
  }


  goToAdminPanel(): void {
    this.router.navigate(['/admin']);
  }

  //  isMyPost = signal<boolean>(false);
  // private setIsMyPost(authorId: number): void {
  //   const userId = this.userId();
  //   this.isMyPost.set(userId !== null && authorId != undefined && authorId === userId)  ;
  // }
  toggleLike(post: Post): void {
    const observable = post.isLikedByCurrentUser
      ? this.postServices.unlikePost(post.id)
      : this.postServices.likePost(post.id);
    observable.subscribe({
      next: () => {
        this.posts.update((posts) =>
          posts.map((p) => {
            if (p.id === post.id) {
              return {
                ...p,
                isLikedByCurrentUser: !p.isLikedByCurrentUser,
                likesCount: p.isLikedByCurrentUser
                  ? (p.likesCount || 1) - 1
                  : (p.likesCount || 0) + 1,
              };
            }
            return p;
          }),
        );
      },
      error: (error) => {
        console.log('Error toggling like:', error);
      },
    });
  }

  viewPost(postId: number): void {
    this.router.navigate(['/posts', postId]);
  }
  viewProfile(username: string): void {
    this.router.navigate(['/profile', username]);
  }
  discoverUsers(): void {
    this.router.navigate(['/discover']);
  }
  home(): void {
    console.log('im here!!!!');
    this.router.navigate(['home']);
  }

  createpost(): void {
    this.router.navigate(['posts/create']);
  }

  logout(): void {
    this.authService.logout();
  }
  getMediaUrl(filename: string): string {
    return this.postServices.getMediaUrl(filename);
  }

  reportPost(post: Post): void {
    const dialogRef = this.dialog.open(ReportDialogComponent, {
      width: '600px',
      maxWidth: '95vw',
      data: {
        postId: post.id,
        postTitle: post.title,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // User submitted the report
        this.reportService.reportPost(result).subscribe({
          next: () => {
            this.snackBar.open('Report submitted successfully. Our team will review it.', 'Close', {
              duration: 5000,
            });
          },
          error: (error) => {
            console.error('Error submitting report:', error);
            const message = error.error?.message || 'Failed to submit report. Please try again.';
            this.snackBar.open(message, 'Close', { duration: 5000 });
          },
        });
      }
    });
  }
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;

    return date.toLocaleDateString();
  }
}
