import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  sessionService = inject(SessionService);
  private apiService = inject(ApiService);
  private authService = inject(AuthService);
  private router = inject(Router);

  newAnalysis(): void {
    this.sessionService.clearActive();
  }

  selectSession(id: string): void {
    this.sessionService.isLoading.set(true);
    this.apiService.getSession(id).subscribe({
      next: (session) => {
        this.sessionService.activeSession.set(session);
        this.apiService.getPropertyDetails(id).subscribe({
          next: (details) => {
            this.sessionService.activePropertyDetails.set(details);
            this.sessionService.isLoading.set(false);
          },
          error: () => this.sessionService.isLoading.set(false),
        });
      },
      error: () => this.sessionService.isLoading.set(false),
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
