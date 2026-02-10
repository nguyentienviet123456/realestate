import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PropertyField } from '../../../core/models/property-details.model';
import { StatusBadgeComponent } from './status-badge.component';

@Component({
  selector: 'app-field-row',
  standalone: true,
  imports: [CommonModule, StatusBadgeComponent],
  templateUrl: './field-row.component.html',
})
export class FieldRowComponent {
  @Input({ required: true }) field!: PropertyField;
}
