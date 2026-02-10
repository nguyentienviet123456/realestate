import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private credentials = signal<{ username: string; password: string } | null>(null);

  isAuthenticated = computed(() => this.credentials() !== null);

  login(username: string, password: string): void {
    this.credentials.set({ username, password });
    sessionStorage.setItem('auth', btoa(`${username}:${password}`));
  }

  logout(): void {
    this.credentials.set(null);
    sessionStorage.removeItem('auth');
  }

  getAuthHeader(): string | null {
    const stored = sessionStorage.getItem('auth');
    return stored ? `Basic ${stored}` : null;
  }

  restoreSession(): void {
    const stored = sessionStorage.getItem('auth');
    if (stored) {
      try {
        const decoded = atob(stored);
        const [username, ...rest] = decoded.split(':');
        const password = rest.join(':');
        this.credentials.set({ username, password });
      } catch {
        sessionStorage.removeItem('auth');
      }
    }
  }
}
