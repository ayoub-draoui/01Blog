import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { AdminService, DashboardStats,} from '../../../core/services/admin.service';
import { Report } from '../../../shared/models/reports.model';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../shared/models/user.model';
import { Post } from '../../../shared/models/post.model';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatTableModule,
    MatMenuModule,
    MatChipsModule,
    MatDialogModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltipModule,
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss',
})
export class AdminDashboardComponent implements OnInit {
  
  
  private authService = inject(AuthService); 
  stats = signal<DashboardStats | null>(null);
  users = signal<User[]>([]);
  posts = signal<Post[]>([]);
  reports = signal<Report[]>([]);
  isLoadingStats = signal(false);
  isLoadingUsers = signal(false);
  isLoadingPosts = signal(false);
  isLoadingReports = signal(false);
  searchQuery = signal('');
  
  userColumns = ['id', 'username', 'email', 'role', 'status', 'actions'];
  postColumns = ['id', 'title', 'author', 'createdAt', 'actions'];
  reportColumns = ['id', 'type', 'reporter', 'reported', 'status', 'actions'];
  
  constructor(
    private adminService: AdminService,
    
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}
  
  ngOnInit(): void {
    console.log("i reached this point this is comming from admin compooo !!!!!");
    this.loadDashboardStats();
    this.loadUsers();
    this.loadPosts();
    this.loadReports();
  }

  loadDashboardStats(): void {
    this.isLoadingStats.set(true);
    this.adminService.getDashboardStats().subscribe({
      next: (stats) => {
        console.log("this is comming from load stats");
        
        this.stats.set(stats);
        this.isLoadingStats.set(false);
      },
      error: (error) => {
        console.log('Error loading stats:', error);
        this.isLoadingStats.set(false);
      },
    });
  }

  loadUsers(): void {
    this.isLoadingUsers.set(true);
    this.adminService.getAllUsers(0, 50).subscribe({
      next: (response) => {
        this.users.set(response.content);
        this.isLoadingUsers.set(false);
      },
      error: (error) => {
        console.log('Error loading users:', error);
        this.isLoadingUsers.set(false);
      },
    });
  }

  loadPosts(): void {
    this.isLoadingPosts.set(true);
    this.adminService.getAllPosts(0, 50).subscribe({
      next: (response) => {
        this.posts.set(response.content);
        this.isLoadingPosts.set(false);
      },
      error: (error) => {
        console.log('Error loading posts:', error);
        this.isLoadingPosts.set(false);
      },
    });
  }



  toggleUserBan(user: User): void {
    const action = user.banned ? 'unban' : 'ban';
    
    // Using a simple confirm for now, but ensure we pass the object to track state
    if (!confirm(`Are you sure you want to ${action} ${user.username}?`)) return;

    this.adminService.toggleUserBan(user.id).subscribe({
      next: () => {
        // Optimistic update or refresh
        this.snackBar.open(`User ${user.username} ${action}ned!`, 'Close', { duration: 3000 });
        this.loadUsers(); // Refresh the list
        this.loadDashboardStats(); // Update the pending reports/stats if linked
      },
      error: (err) => {
        console.error('Ban failed', err);
        this.snackBar.open(`Failed to ${action} user.`, 'Close', { duration: 3000 });
      }
    });
  }

  loadReports(): void {
    this.isLoadingReports.set(true);
    this.adminService.getPendingReports(0, 50).subscribe({
      next: (response) => {
        this.reports.set(response.content);
        this.isLoadingReports.set(false);
        console.log("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn",this.reports); 
        
      },
      error: (error) => {
        console.log('Error loading reports:', error);
        this.isLoadingReports.set(false);
      },
    });
  }

  deleteUser(userId: number, username: string): void {
    if (
      !confirm(
        `Are you sure you want to delete user "${username}"? This will delete all their posts, comments, and data.`,
      )
    ) {
      return;
    }

    this.adminService.deleteUser(userId).subscribe({
      next: () => {
        this.snackBar.open(`User "${username}" deleted successfully`, 'Close', { duration: 3000 });
        this.loadUsers();
        this.loadDashboardStats();
      },
      error: (error) => {
        console.error('Error deleting user:', error);
        this.snackBar.open('Failed to delete user', 'Close', { duration: 3000 });
      },
    });
  }

  deletePost(postId: number, title: string): void {
    if (!confirm(`Are you sure you want to delete post "${title}"?`)) {
      return;
    }

    this.adminService.deletePost(postId).subscribe({
      next: () => {
        this.snackBar.open('Post deleted successfully', 'Close', { duration: 3000 });
        this.loadPosts();
        this.loadDashboardStats();
      },
      error: (error) => {
        console.error('Error deleting post:', error);
        this.snackBar.open('Failed to delete post', 'Close', { duration: 3000 });
      },
    });
  }

  updateReportStatus(report: Report, status: string): void {
    this.adminService.updateReport(report.id, status).subscribe({
      next: () => {
        this.snackBar.open('Report updated successfully', 'Close', { duration: 3000 });
        this.loadReports();
        this.loadDashboardStats();
      },
      error: (error) => {
        console.log('Error updating report:', error);
        this.snackBar.open('Failed to update report', 'Close', { duration: 3000 });
      },
    });
  }

  viewUser(userId: number): void {
    this.adminService.getUserById(userId).subscribe({
      next: (user) => {
        console.log('User details:', user);
        this.router.navigate(['/profile', user.username]);
      },
      error: (error) => {
        console.log('Error fetching user details:', error);
      },
    });
    console.log('View user:', userId);
  }

  viewPost(postId: number): void {
    this.router.navigate(['/posts', postId]);
  }

  logout(): void {
    this.authService.logout();
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'warn';
      case 'REVIEWED':
        return 'accent';
      case 'RESOLVED':
        return 'primary';
      case 'DISMISSED':
        return '';
      default:
        return '';
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
