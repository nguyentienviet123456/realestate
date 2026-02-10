import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryGroup } from '../../../core/models/property-details.model';
import { FieldRowComponent } from './field-row.component';

@Component({
  selector: 'app-category-group',
  standalone: true,
  imports: [CommonModule, FieldRowComponent],
  templateUrl: './category-group.component.html',
})
export class CategoryGroupComponent {
  @Input({ required: true }) group!: CategoryGroup;
  @Input() index = 0;
  isCollapsed = true;

  toggle(): void {
    this.isCollapsed = !this.isCollapsed;
  }
}
