import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

export interface ReportDialogData {
  postId: number;
  postTitle: string;
}

@Component({
  selector: 'app-report-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './report-dialog.component.html',
  styleUrl: './report-dialog.component.scss'
})
export class ReportDialogComponent {
  reportForm: FormGroup;
  isSubmitting = signal(false);

  // Predefined report reasons
  reportReasons = [
    { value: 'spam', label: 'Spam or misleading' },
    { value: 'harassment', label: 'Harassment or bullying' },
    { value: 'hate-speech', label: 'Hate speech or discrimination' },
    { value: 'violence', label: 'Violence or dangerous content' },
    { value: 'inappropriate', label: 'Inappropriate or offensive' },
    { value: 'copyright', label: 'Copyright infringement' },
    { value: 'misinformation', label: 'False information' },
    { value: 'other', label: 'Other (please specify)' }
  ];

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ReportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReportDialogData
  ) {
    this.reportForm = this.fb.group({
      category: ['', Validators.required],
      reason: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(1000)
      ]]
    });

    // Auto-fill reason when category is selected (except "other")
    this.reportForm.get('category')?.valueChanges.subscribe(value => {
      if (value && value !== 'other') {
        const selectedReason = this.reportReasons.find(r => r.value === value);
        if (selectedReason) {
          this.reportForm.patchValue({
            reason: `I am reporting this post because it contains ${selectedReason.label.toLowerCase()}.`
          });
        }
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.reportForm.valid && !this.isSubmitting()) {
      this.isSubmitting.set(true);
      
      const reportData = {
        reportedPostId: this.data.postId,
        reason: this.reportForm.value.reason
      };

      this.dialogRef.close(reportData);
    }
  }

  get category() {
    return this.reportForm.get('category');
  }

  get reason() {
    return this.reportForm.get('reason');
  }
}