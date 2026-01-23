import { Component, OnInit, signal, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NotificationService, Notification } from '../../../core/services/notification.service';
import { PostService } from '../../../core/services/post.service';

@Component({
  selector: 'app-notification-panel',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatBadgeModule,
    MatMenuModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './notification-panel.component.html',
  styleUrl: './notification-panel.component.scss',
})
export class NotificationPanelComponent implements OnInit {
  private notificationService = inject(NotificationService);
  private postService = inject(PostService);
  private router = inject(Router);

  @Output() notificationClick = new EventEmitter<void>();

  notifications = signal<Notification[]>([]);
  isLoading = signal(false);
  unreadCount = this.notificationService.unreadCount;

  ngOnInit(): void {
    this.loadUnreadCount();
    this.startPolling();
  }
  loadNotifications(): void {
    this.isLoading.set(true);
    this.notificationService.getNotifications(0, 10).subscribe({
      next: (response) => {
        this.notifications.set(response.content);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.log('an error have occured trying to reach the notif', error);
        this.isLoading.set(false);
      },
    });
  }

  loadUnreadCount(): void {
    this.notificationService.getUnreadCount().subscribe({
      error: (error) => console.log('erro bringing l count', error),
    });
  }

  startPolling(): void {
    this.notificationService.startPolling().subscribe();
  }
  onMenuOpened(): void {
    this.loadNotifications();
  }

  markAsRead(notification: Notification, event: Event): void {
    event.stopPropagation();
    if (notification.isRead) return;
    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        this.notifications.update((notifications) =>
          notifications.map((n) => (n.id === notification.id ? { ...n, isRead: true } : n))
        );
        this.loadUnreadCount();
      }, error : (error) => {
                console.log("errro trying to mark a notif as read", error);   
      } 
    });
  }



   markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.update(notifications =>
          notifications.map(n => ({ ...n, isRead: true }))
        );

        this.unreadCount.set(0);
      },
      error: (error) => console.error('Error marking all as read:', error)
    });
  }



  handleNotificationClick(notification: Notification) : void {
    this.markAsRead(notification, new Event("click"));
    switch(notification.type){
        case"COMMENT":
        case "LIKE":
        case "NEW_POST":
            if (notification.relatedPostId){
                this.router.navigate(["/posts", notification.relatedPostId])
            }
            break
        case "NEW_FOLLOWER":
            this.router.navigate(["/profile", notification.actorUsername])
            break
    }
    this.notificationClick.emit;
  }

   viewAllNotifications(): void {
    this.router.navigate(['/notifications']);
    this.notificationClick.emit();
  }

  getNotificationIcon(type: string): string{
    switch(type){
        case "NEW_POST": return 'article'
        case 'NEW_FOLLOWER': return 'person_add'
        case 'LIKE': return 'favorite'
        case 'COMMENT': return 'comment'
        default: return 'notifications'

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
