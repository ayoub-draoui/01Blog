import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../../core/services/auth.service';
import { PostService } from '../../../core/services/post.service';
import { NotificationPanelComponent } from '../notification-panel/notification-panel.component';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    NotificationPanelComponent,
    MatDividerModule
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  private authService = inject(AuthService);
  private postService = inject(PostService);
  private router = inject(Router);

  currentUser = this.authService.currentUser;
  isAdmin = computed(() => this.currentUser()?.role === 'ROLE_ADMIN');

  navigateHome(): void {
    this.router.navigate(['/home']);
  }

  createPost(): void {
    this.router.navigate(['/posts/create']);
  }

  discoverUsers(): void {
    this.router.navigate(['/discover']);
  }

  viewNotifications(): void {
    this.router.navigate(['/notifications']);
  }

  viewProfile(): void {
    const username = this.currentUser()?.username;
    if (username) {
      this.router.navigate(['/profile', username]);
    }
  }

  goToAdminPanel(): void {
    this.router.navigate(['/admin']);
  }

  logout(): void {
    this.authService.logout();
  }

  switchTheme(theme: 'light' | 'dark'): void {
    document.body.classList.remove('light', 'dark');
    document.body.classList.add(theme);
  }

  getMediaUrl(filename: string): string {
    return this.postService.getMediaUrl(filename);
  }
}