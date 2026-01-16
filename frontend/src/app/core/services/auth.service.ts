import { Injectable, signal, computed, effect } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environments/environment';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  DecodedToken,
} from '../../shared/models/auth.model';
import { User } from '../../shared/models/user.model';
 

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'current_user';

  private tokenSignal = signal<string | null>(this.getStoredToken());
  private currentUserSignal = signal<User | null>(this.getStoredUser());
  public isAuthenticated = computed(() => !!this.tokenSignal());
  public currentUser = computed(() => this.currentUserSignal());
  public isAdmin = computed(() => this.currentUserSignal()?.role === 'ROLE_ADMIN');
  public isUser = computed(() => this.currentUserSignal()?.role === 'ROLE_USER');
  constructor(private http: HttpClient, private router: Router) {
    effect(() => {
      console.log('Auth State:', {
        isAuthenticated: this.isAuthenticated(),
        user: this.currentUser(),
      });
    });
  }
  login(credentials: LoginRequest): Observable<AuthResponse> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.auth.login}`;
    return this.http.post<AuthResponse>(url, credentials).pipe(
      tap((response) => this.handleAuthResponse(response)),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }



  register(userData: RegisterRequest): Observable<AuthResponse> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.auth.register}`;
    
    return this.http.post<AuthResponse>(url, userData).pipe(
      tap(response => this.handleAuthResponse(response)),
      catchError(error => {
        console.error('Registration error:', error);
        return throwError(() => error);
      })
    );
  }


   logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    
    this.tokenSignal.set(null);
    this.currentUserSignal.set(null);
    
    this.router.navigate(['/auth/login']);
  }

    getToken(): string | null {
    return this.tokenSignal();
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      const currentTime = Date.now() / 1000;
      return decoded.exp < currentTime;
    } catch (error) {
      return true;
    }
  }


  decodeToken(): DecodedToken | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      return jwtDecode<DecodedToken>(token);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }
  refreshCurrentUser(): Observable<User> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.users.me}`;
    
    return this.http.get<User>(url).pipe(
      tap(user => {
        this.currentUserSignal.set(user);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
      }),
      catchError(error => {
        console.error('Error refreshing user:', error);
        return throwError(() => error);
      })
    );
  }
  

  private handleAuthResponse(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    this.tokenSignal.set(response.token);

    const user: User = {
      id: response.userId,
      username: response.username,
      email: response.email,
      firstname: response.firstname,
      lastname: response.lastname,
      avatar: response.avatar,
      role: response.role
    };

    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUserSignal.set(user);
  }
    private getStoredToken(): string | null {
    if (typeof window !== 'undefined' && window.localStorage) {
      return localStorage.getItem(this.TOKEN_KEY);
    }
    return null;
  }
  private getStoredUser(): User | null {
    if (typeof window !== 'undefined' && window.localStorage) {
      const userJson = localStorage.getItem(this.USER_KEY);
      return userJson ? JSON.parse(userJson) : null;
    }
    return null;
  }



}
