import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatSession, ChatSessionSummary } from '../models/chat-session.model';
import { PropertyDetails } from '../models/property-details.model';
import { AnalyzeResponse } from '../models/analyze-response.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = '/api';

  constructor(private http: HttpClient) {}

  analyzePdf(file: File, sessionId?: string): Observable<AnalyzeResponse> {
    const formData = new FormData();
    formData.append('file', file);
    if (sessionId) {
      formData.append('sessionId', sessionId);
    }
    return this.http.post<AnalyzeResponse>(`${this.baseUrl}/analyze`, formData);
  }

  getSessions(): Observable<ChatSessionSummary[]> {
    return this.http.get<ChatSessionSummary[]>(`${this.baseUrl}/sessions`);
  }

  getSession(id: string): Observable<ChatSession> {
    return this.http.get<ChatSession>(`${this.baseUrl}/sessions/${id}`);
  }

  getPropertyDetails(sessionId: string): Observable<PropertyDetails> {
    return this.http.get<PropertyDetails>(`${this.baseUrl}/sessions/${sessionId}/property`);
  }
}
