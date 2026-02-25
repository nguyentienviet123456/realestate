import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatSession, ChatSessionSummary } from '../models/chat-session.model';
import { PropertyDetails } from '../models/property-details.model';
import { ExtractResponse } from '../models/analyze-response.model';

export interface LoginResponse {
  success: boolean;
  errorCode: string | null;
  displayName: string | null;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/auth/login`, { username, password });
  }

  extractPdf(file: File, sessionId?: string): Observable<ExtractResponse> {
    const formData = new FormData();
    formData.append('file', file);
    if (sessionId) {
      formData.append('sessionId', sessionId);
    }
    return this.http.post<ExtractResponse>(`${this.baseUrl}/extract`, formData);
  }

  getSessions(days?: number): Observable<ChatSessionSummary[]> {
    const params: Record<string, string> = {};
    if (days) {
      params['days'] = days.toString();
    }
    return this.http.get<ChatSessionSummary[]>(`${this.baseUrl}/sessions`, { params });
  }

  getSession(id: string): Observable<ChatSession> {
    return this.http.get<ChatSession>(`${this.baseUrl}/sessions/${id}`);
  }

  getPropertyDetails(sessionId: string): Observable<PropertyDetails> {
    return this.http.get<PropertyDetails>(`${this.baseUrl}/sessions/${sessionId}/property`);
  }

  sendMessage(sessionId: string, content: string): Observable<ChatSession> {
    return this.http.post<ChatSession>(`${this.baseUrl}/sessions/${sessionId}/messages`, { content });
  }
}
