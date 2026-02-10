import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { SessionService } from '../../../core/services/session.service';
import { AnalyzeResponse } from '../../../core/models/analyze-response.model';

@Component({
  selector: 'app-pdf-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pdf-upload.component.html',
})
export class PdfUploadComponent {
  @Input() sessionId?: string;
  @Input() compact = false;
  @Output() analysisComplete = new EventEmitter<AnalyzeResponse>();

  isDragging = false;
  isUploading = false;
  selectedFileName = '';
  chatInput = '';

  constructor(
    private apiService: ApiService,
    private sessionService: SessionService,
  ) {}

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFile(files[0]);
    }
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  private handleFile(file: File): void {
    if (!file.name.toLowerCase().endsWith('.pdf')) {
      return;
    }

    this.selectedFileName = file.name;
    this.isUploading = true;
    this.sessionService.isLoading.set(true);

    this.apiService.analyzePdf(file, this.sessionId).subscribe({
      next: (response) => {
        this.isUploading = false;
        this.selectedFileName = '';
        this.sessionService.isLoading.set(false);
        this.analysisComplete.emit(response);
      },
      error: () => {
        this.isUploading = false;
        this.sessionService.isLoading.set(false);
      },
    });
  }
}
