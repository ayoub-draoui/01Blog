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
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  // {
  //   path: 'home',
  //   canActivate: [authGuard],
  //   loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  // },
//   {
//     path: 'profile/:username',
//     canActivate: [authGuard],
//     loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
//   },
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