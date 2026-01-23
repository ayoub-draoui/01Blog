import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { NotificationService, Notification } from '../../../core/services/notification.service';
import { PostService } from '../../../core/services/post.service';

@Component({
  selector: 'app-notifications-page',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatChipsModule
  ],
  templateUrl: './notifications-page.component.html',
  styleUrl: './notifications-page.component.scss'
})
export class NotificationsPageComponent implements OnInit {
  private notificationService = inject(NotificationService);
  private postService = inject(PostService);
  private router = inject(Router);

  // Signals
  allNotifications = signal<Notification[]>([]);
  unreadNotifications = signal<Notification[]>([]);
  isLoadingAll = signal(false);
  isLoadingUnread = signal(false);
  currentTab = signal<'all' | 'unread'>('all');

  ngOnInit(): void {
    this.loadAllNotifications();
    this.loadUnreadNotifications();
  }

  loadAllNotifications(): void {
    this.isLoadingAll.set(true);

    this.notificationService.getNotifications(0, 50).subscribe({
      next: (response) => {
        this.allNotifications.set(response.content);
        this.isLoadingAll.set(false);
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.isLoadingAll.set(false);
      }
    });
  }

  loadUnreadNotifications(): void {
    this.isLoadingUnread.set(true);

    this.notificationService.getUnreadNotifications(0, 50).subscribe({
      next: (response) => {
        this.unreadNotifications.set(response.content);
        this.isLoadingUnread.set(false);
      },
      error: (error) => {
        console.error('Error loading unread notifications:', error);
        this.isLoadingUnread.set(false);
      }
    });
  }

  markAsRead(notification: Notification): void {
    if (notification.isRead) return;

    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        // Update both lists
        this.allNotifications.update(notifications =>
          notifications.map(n =>
            n.id === notification.id ? { ...n, isRead: true } : n
          )
        );

        this.unreadNotifications.update(notifications =>
          notifications.filter(n => n.id !== notification.id)
        );
      },
      error: (error) => console.error('Error marking as read:', error)
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.allNotifications.update(notifications =>
          notifications.map(n => ({ ...n, isRead: true }))
        );
        this.unreadNotifications.set([]);
      },
      error: (error) => console.error('Error marking all as read:', error)
    });
  }

  handleNotificationClick(notification: Notification): void {
    this.markAsRead(notification);

    switch (notification.type) {
      case 'NEW_POST':
      case 'LIKE':
      case 'COMMENT':
        if (notification.relatedPostId) {
          this.router.navigate(['/posts', notification.relatedPostId]);
        }
        break;
      case 'NEW_FOLLOWER':
        this.router.navigate(['/profile', notification.actorUsername]);
        break;
    }
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'NEW_POST': return 'article';
      case 'NEW_FOLLOWER': return 'person_add';
      case 'LIKE': return 'favorite';
      case 'COMMENT': return 'comment';
      default: return 'notifications';
    }
  }

  getNotificationColor(type: string): string {
    switch (type) {
      case 'NEW_POST': return 'primary';
      case 'NEW_FOLLOWER': return 'accent';
      case 'LIKE': return 'warn';
      case 'COMMENT': return 'primary';
      default: return '';
    }
  }

  getAvatarUrl(avatar: string | null): string {
    if (!avatar) return '';
    return this.postService.getMediaUrl(avatar);
  }

  formatTimeAgo(dateString: string): string {
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