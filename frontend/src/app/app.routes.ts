import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
 
export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full',
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent:  () =>
       import('./features/admin/admin-dashboard/admin-dashboard.component').then(
        (m) => m.AdminDashboardComponent,
      )
    },
    // component: AdminDashboardComponent
  
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login.component').then((m) => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
      },
    ],
  },
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () => import('./features/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'posts/create',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/posts/create-post/create-post.component').then(
        (m) => m.CreatePostComponent,
      ),
  },
  {
    path: 'posts/edit/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/posts/edit-post/edit-post.component').then(
        (m) => m.EditPostComponent,
      ),
  },
  {
    path: 'posts/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/posts/post-detail/post-detail.component').then(
        (m) => m.PostDetailComponent,
      ),
  },
  {
    path: 'profile-edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/edit-profile/edit-profile.component').then(
        (m) => m.EditProfileComponent,
      ),
  },
  {
    path: 'profile/:username',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/profile.component').then((m) => m.ProfileComponent),
  },
  {
    path: 'discover',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/users/discover-users/discover-users.component').then(
        (m) => m.DiscoverUsersComponent,
      ),
  },
  {
    path: 'notifications',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/notifications/notifications-page/notifications-page.component').then(
        (m) => m.NotificationsPageComponent,
      ),
  },
  {
    path: '**',
    redirectTo: '/home',
  },
];
