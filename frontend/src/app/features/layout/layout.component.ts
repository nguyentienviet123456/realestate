import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { ChatComponent } from '../chat/chat.component';
import { AttributePanelComponent } from '../attribute-panel/attribute-panel.component';
import { ApiService } from '../../core/services/api.service';
import { SessionService } from '../../core/services/session.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, SidebarComponent, ChatComponent, AttributePanelComponent],
  templateUrl: './layout.component.html',
})
export class LayoutComponent implements OnInit {
  constructor(
    private apiService: ApiService,
    private sessionService: SessionService,
  ) {}

  ngOnInit(): void {
    this.loadSessions();
  }

  loadSessions(): void {
    this.apiService.getSessions().subscribe({
      next: (sessions) => this.sessionService.sessions.set(sessions),
    });
  }
}
