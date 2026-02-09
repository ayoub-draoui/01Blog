import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);

  console.log( 'authtriggered for:', state.url);

  if (!isBrowser) {
    console.log('not in browser allowing');
    return true;
  }

  const isAuthenticated = authService.isAuthenticated();
  const isTokenValid = !authService.isTokenExpired();

  console.log('Auth Guard Check:', {
    isAuthenticated,
    isTokenValid,
    currentUser: authService.currentUser()
  });

  if (isAuthenticated && isTokenValid) {
    console.log('auth guard passed');
    return true;
  }

  console.log('❌ Auth guard failed - redirecting to login');
  router.navigate(['/auth/login'], { 
    queryParams: { returnUrl: state.url }
  });
  
  return false;
};