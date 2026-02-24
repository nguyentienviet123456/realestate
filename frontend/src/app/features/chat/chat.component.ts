import { Component, inject, signal, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PdfUploadComponent } from './components/pdf-upload.component';
import { MessageBubbleComponent } from './components/message-bubble.component';
import { SessionService } from '../../core/services/session.service';
import { ApiService } from '../../core/services/api.service';
import { ChatMessage } from '../../core/models/chat-session.model';
import { ExtractResponse } from '../../core/models/analyze-response.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, PdfUploadComponent, MessageBubbleComponent],
  templateUrl: './chat.component.html',
})
export class ChatComponent implements AfterViewChecked {
  sessionService = inject(SessionService);
  private apiService = inject(ApiService);

  messageInput = '';
  sending = signal(false);

  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  private shouldScroll = false;

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  onExtractComplete(response: ExtractResponse): void {
    this.apiService.getSession(response.sessionId).subscribe({
      next: (session) => this.sessionService.activeSession.set(session),
    });

    this.apiService.getPropertyDetails(response.sessionId).subscribe({
      next: (details) => this.sessionService.activePropertyDetails.set(details),
    });

    this.apiService.getSessions().subscribe({
      next: (sessions) => this.sessionService.sessions.set(sessions),
    });

    this.shouldScroll = true;
  }

  onEnterKey(event: Event): void {
    const ke = event as KeyboardEvent;
    if (!ke.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendMessage(): void {
    const content = this.messageInput.trim();
    const session = this.sessionService.activeSession();
    if (!content || !session || this.sending()) return;

    this.sending.set(true);
    this.messageInput = '';

    // Optimistic update: show user message immediately
    const userMessage: ChatMessage = {
      role: 'user',
      content,
      type: 'text',
      timestamp: new Date().toISOString(),
    };
    this.sessionService.activeSession.set({
      ...session,
      messages: [...session.messages, userMessage],
    });
    this.shouldScroll = true;

    this.apiService.sendMessage(session.id, content).subscribe({
      next: (updatedSession) => {
        this.sessionService.activeSession.set(updatedSession);
        this.sending.set(false);
        this.shouldScroll = true;
      },
      error: () => {
        this.sending.set(false);
      },
    });
  }

  private scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop =
        this.messagesContainer.nativeElement.scrollHeight;
    } catch (_) {}
  }
}
