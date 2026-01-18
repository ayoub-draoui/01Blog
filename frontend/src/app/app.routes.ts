import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: 'home',
    canActivate: [authGuard],
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'posts/create',
    canActivate: [authGuard],
    loadComponent: () => import('./features/posts/create-post/create-post.component').then(m => m.CreatePostComponent)
  },

  {
    path: 'profile/:username',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },
//   {
//     path: 'admin',
//     canActivate: [authGuard, adminGuard],
//     loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
//   },
  {
    path: '**',
    redirectTo: '/home'
  }
];