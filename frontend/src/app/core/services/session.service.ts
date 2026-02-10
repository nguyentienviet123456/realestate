import { Injectable, signal, computed } from '@angular/core';
import { ChatSession, ChatSessionSummary } from '../models/chat-session.model';
import { PropertyDetails, PropertyField, CategoryGroup } from '../models/property-details.model';

@Injectable({ providedIn: 'root' })
export class SessionService {
  sessions = signal<ChatSessionSummary[]>([]);
  activeSession = signal<ChatSession | null>(null);
  activePropertyDetails = signal<PropertyDetails | null>(null);
  isLoading = signal<boolean>(false);
  filterStatus = signal<'all' | 'done' | 'pending'>('all');

  categoryGroups = computed<CategoryGroup[]>(() => {
    const details = this.activePropertyDetails();
    const filter = this.filterStatus();
    if (!details) return [];

    const grouped = new Map<string, PropertyField[]>();
    for (const field of details.fields) {
      if (filter !== 'all' && field.status !== filter) continue;
      if (!grouped.has(field.category)) {
        grouped.set(field.category, []);
      }
      grouped.get(field.category)!.push(field);
    }

    return Array.from(grouped.entries()).map(([category, fields]) => ({
      category,
      fields,
      doneCount: fields.filter(f => f.status === 'done').length,
      pendingCount: fields.filter(f => f.status === 'pending').length,
    }));
  });

  stats = computed(() => {
    const details = this.activePropertyDetails();
    if (!details) return { total: 0, done: 0, pending: 0 };
    return {
      total: details.fields.length,
      done: details.fields.filter(f => f.status === 'done').length,
      pending: details.fields.filter(f => f.status === 'pending').length,
    };
  });

  clearActive(): void {
    this.activeSession.set(null);
    this.activePropertyDetails.set(null);
    this.filterStatus.set('all');
  }
}
