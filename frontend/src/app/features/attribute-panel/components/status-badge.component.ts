import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span
      class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium"
      [class.bg-green-100]="status === 'done'"
      [class.text-green-800]="status === 'done'"
      [class.bg-yellow-100]="status === 'pending'"
      [class.text-yellow-800]="status === 'pending'"
    >
      {{ status === 'done' ? 'Done' : 'Pending' }}
    </span>
  `,
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: 'done' | 'pending';
}
