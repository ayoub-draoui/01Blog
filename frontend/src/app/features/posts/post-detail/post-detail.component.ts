import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDividerModule } from '@angular/material/divider';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AuthService } from '../../../core/services/auth.service';
import { PostService } from '../../../core/services/post.service';
import { Post } from '../../../shared/models/post.model';
import { CommentWithUser } from '../../../shared/models/comment.model';
import { ReportService } from '../../../core/services/report.service';
import { ReportDialogComponent } from '../../../shared/components/reports/report-dialog.component';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    // RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatDividerModule,
    MatMenuModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.scss'
})
export class PostDetailComponent implements OnInit {
  // Inject services
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private postService = inject(PostService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private reportService = inject(ReportService);

  // Signals
  post = signal<Post | null>(null);
  comments = signal<CommentWithUser[]>([]);
  isLoadingPost = signal(false);
  isLoadingComments = signal(false);
  isSubmittingComment = signal(false);
  errorMessage = signal<string | null>(null);
  isAuthor = signal(false);

  // Form
  commentForm: FormGroup;


  currentUser = this.authService.currentUser;
  userid = computed(() => {
    console.log("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk im here");
    
    // const post = this.post();
    const user = this.currentUser();
    // console.log(post);
    // console.log(user?.id);
    return  user?.id;
  });
  
  
  constructor() {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(2000)]]
    });
  }
  
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const postId = +params['id'];
      if (postId) {
        this.loadPost(postId);
        this.loadComments(postId);
      }
    });
  }
  
  loadPost(postId: number): void {
    console.log("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm",this.isAuthor());
    this.isLoadingPost.set(true);
    this.errorMessage.set(null);

    this.postService.getPostById(postId).subscribe({
      next: (post) => {
        this.post.set(post);
        this.isAuthor.set(this.currentUser()?.username === post.authorUsername);
        this.isLoadingPost.set(false);
        
      },
      error: (error) => {
        console.error('Error loading post:', error);
        this.errorMessage.set('Failed to load post');
        this.isLoadingPost.set(false);
      }
    });
    console.log("333333333333333333333333333333333",this.comments);

  }

  loadComments(postId: number): void {
    this.isLoadingComments.set(true);

    this.postService.getComments(postId).subscribe({
      next: (response) => {
        console.warn(response);
        this.comments.set(response.content || []);
        console.log( "ccccccccccccccccccccccccccccccccccccccccccccccccccccc",this.comments());
        
        console.warn(response.content);

        this.isLoadingComments.set(false);
      },
      error: (error) => {
        console.error('Error loading comments:', error);
        this.isLoadingComments.set(false);
      }
    }); 
  }

  toggleLike(): void {
    const post = this.post();
    if (!post) return;

    const observable = post.isLikedByCurrentUser
      ? this.postService.unlikePost(post.id)
      : this.postService.likePost(post.id);

    observable.subscribe({
      next: () => {
        this.post.update(current => {
          if (!current) return current;
          return {
            ...current,
            isLikedByCurrentUser: !current.isLikedByCurrentUser,
            likesCount: current.isLikedByCurrentUser
              ? (current.likesCount || 1) - 1
              : (current.likesCount || 0) + 1
          };
        });
      },
      error: (error) => {
        console.error('Error toggling like:', error);
        this.snackBar.open('Failed to update like', 'Close', { duration: 3000 });
      }
    });
  }

  submitComment(): void {
    if (this.commentForm.invalid || !this.post()) return;

    this.isSubmittingComment.set(true);

    const content = this.commentForm.value.content;
    const postId = this.post()!.id;

    this.postService.addComment(postId, content).subscribe({
      next: (newComment) => {
        // Add new comment to list
        this.comments.update(comments => [newComment, ...comments]);
        
        // Update comment count
        this.post.update(current => {
          if (!current) return current;
          return {
            ...current,
            commentsCount: (current.commentsCount || 0) + 1
          };
        });

        // Reset form
        this.commentForm.reset();
        this.isSubmittingComment.set(false);
        this.snackBar.open('Comment added!', 'Close', { duration: 2000 });
      },
      error: (error) => {
        console.error('Error adding comment:', error);
        this.isSubmittingComment.set(false);
        this.snackBar.open('Failed to add comment', 'Close', { duration: 3000 });
      }
    });
  }
   reportPost(): void {
    const post = this.post();
    if (!post) return;

    const dialogRef = this.dialog.open(ReportDialogComponent
      , {
      width: '600px',
      maxWidth: '95vw',
      data: {
        postId: post.id,
        postTitle: post.title
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // User submitted the report
        this.reportService.reportPost(result).subscribe({
          next: () => {
            this.snackBar.open(
              'Report submitted successfully. Our team will review it.', 
              'Close', 
              { duration: 5000 }
            );
          },
          error: (error) => {
            console.error('Error submitting report:', error);
            const message = error.error?.message || 'Failed to submit report. Please try again.';
            this.snackBar.open(message, 'Close', { duration: 5000 });
          }
        });
      }
    });
  }
  



  reportUser(): void {
    const post = this.post();
    if (!post) return;

    const dialogRef = this.dialog.open(ReportDialogComponent, {
      width: '600px',
      maxWidth: '95vw',
      data: {
        type: 'USER',
        targetId: post.authorId,
        targetTitle: `${post.authorFirstname} ${post.authorLastname}`,
        targetDescription: `@${post.authorUsername}`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Use the generic submitReport method
        this.reportService.submitReport(result.type, result.targetId, result.reason).subscribe({
          next: () => {
            this.snackBar.open(
              'User report submitted successfully. Our team will review it.', 
              'Close', 
              { duration: 5000 }
            );
          },
          error: (error) => {
            console.error('Error submitting user report:', error);
            const message = error.error?.message || 'Failed to submit report. Please try again.';
            this.snackBar.open(message, 'Close', { duration: 5000 });
          }
        });
      }
    });
  }


  deletePost(): void {
    const post = this.post();
    if (!post) return;

    if (!confirm(`Are you sure you want to delete "${post.title}"?`)) {
      return;
    }
    const username = post.authorUsername;
    
    // console.log("333333333333333333333333333333333333333333333333333333333333333330",id);
    console.log("333333333333333333333333333333333333333333333333333333333333333330",username);
    console.log("333333333333333333333333333333333333333333333333333333333333333330",post);
    

    this.postService.deletePost(post.id).subscribe({
      next: () => {
        this.snackBar.open('Post deleted successfully', 'Close', { duration: 3000 });
        this.router.navigate(['/profile', username]);    
      },
      error: (error) => {
        console.error('Error deleting post:', error);
        this.snackBar.open('Failed to delete post', 'Close', { duration: 3000 });
      }
    });
  }

  editPost(): void {
    const post = this.post();
    if (!post) return;
    this.router.navigate(['/posts/edit', post.id]);
  }

  viewProfile(username: string): void {
    this.router.navigate(['/profile', username]);
  }

  sharePost(): void {
    const post = this.post();
    if (!post) return;

    const url = window.location.href;
    navigator.clipboard.writeText(url).then(() => {
      this.snackBar.open('Link copied to clipboard!', 'Close', { duration: 2000 });
    });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  getMediaUrl(filename: string): string {
    return this.postService.getMediaUrl(filename);
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

  get content() {
    return this.commentForm.get('content');
  }
}