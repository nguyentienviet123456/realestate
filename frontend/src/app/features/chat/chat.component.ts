import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PdfUploadComponent } from './components/pdf-upload.component';
import { MessageBubbleComponent } from './components/message-bubble.component';
import { SessionService } from '../../core/services/session.service';
import { ApiService } from '../../core/services/api.service';
import { AnalyzeResponse } from '../../core/models/analyze-response.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, PdfUploadComponent, MessageBubbleComponent],
  templateUrl: './chat.component.html',
})
export class ChatComponent {
  sessionService = inject(SessionService);
  private apiService = inject(ApiService);

  onAnalysisComplete(response: AnalyzeResponse): void {
    // Reload session data
    this.apiService.getSession(response.sessionId).subscribe({
      next: (session) => {
        this.sessionService.activeSession.set(session);
        this.apiService.getPropertyDetails(response.sessionId).subscribe({
          next: (details) => {
            this.sessionService.activePropertyDetails.set(details);
          },
        });
      },
    });

    // Refresh session list
    this.apiService.getSessions().subscribe({
      next: (sessions) => this.sessionService.sessions.set(sessions),
    });
  }
}
