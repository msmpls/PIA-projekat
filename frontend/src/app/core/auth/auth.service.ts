import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/auth.model';

const STORAGE_KEY = 'auth_data';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = 'http://localhost:8080/api/auth';

  currentUser = signal<AuthResponse | null>(this.getAuthData());

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => this.setSession(response))
    );
  }

  register(formData: FormData): Observable<AuthResponse> {
    // RegisterRequest se šalje kao multipart/form-data zbog profile_image fajla
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, formData);
  }

  private setSession(response: AuthResponse): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(response));
    this.currentUser.set(response);
  }

  logout(): void {
    this.currentUser.set(null);
    localStorage.removeItem(STORAGE_KEY);
    this.router.navigate(['/login']);
  }

  getAuthData(): AuthResponse | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  }

  getToken(): string | null {
    return this.currentUser()?.token ?? null; // Koristimo signal
  }

  getRole(): string | null {
    return this.currentUser()?.role ?? null; // Koristimo signal
  }

  isLoggedIn(): boolean {
    const data = this.currentUser(); // Koristimo signal
    if (!data?.token) return false;
    return !this.isTokenExpired(data.token);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiryMs = payload.exp * 1000; // JWT 'exp' je u sekundama
      return Date.now() >= expiryMs;
    } catch {
      return true; // ako ne može da se parsira, tretiramo kao istekao
    }
  }
}