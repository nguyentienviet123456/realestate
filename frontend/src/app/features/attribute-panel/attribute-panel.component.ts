import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SessionService } from '../../core/services/session.service';
import { CategoryGroupComponent } from './components/category-group.component';

@Component({
  selector: 'app-attribute-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, CategoryGroupComponent],
  templateUrl: './attribute-panel.component.html',
})
export class AttributePanelComponent {
  sessionService = inject(SessionService);

  onFilterChange(value: string): void {
    this.sessionService.filterStatus.set(value as 'all' | 'done' | 'pending');
  }
}
