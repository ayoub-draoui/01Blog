import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  console.log('🔒 ADMIN GUARD CALLED - State URL:', state.url);
  
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);

  console.log('isBrowser:', isBrowser);

  if (!isBrowser) {
    console.log('⚠️ Not in browser - returning true');
    return true;
  }

  const isAuthenticated = authService.isAuthenticated();
  const isTokenValid = !authService.isTokenExpired();
  const isAdmin = authService.isAdmin();
  const user = authService.currentUser();

  console.log('Admin Guard Checks:', {
    isAuthenticated,
    isTokenValid,
    isAdmin,
    userRole: user?.role,
    user
  });

  if (isAuthenticated && isTokenValid && isAdmin) {
    console.log('✅ ADMIN GUARD PASSED - Loading admin dashboard');
    return true;
  }

  console.log('❌ ADMIN GUARD FAILED');
  console.log('Reason:', {
    notAuthenticated: !isAuthenticated,
    tokenExpired: !isTokenValid,
    notAdmin: !isAdmin
  });
  
  console.log('Redirecting to home...');
  router.navigate(['/']);
  return false;
};