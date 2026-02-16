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
  type: 'POST' | 'USER';
  targetId: number;
  targetTitle: string; 
  targetDescription?: string;  
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

  postReasons = [
    { value: 'spam', label: 'Spam or misleading' },
    { value: 'harassment', label: 'Harassment or bullying' },
    { value: 'hate-speech', label: 'Hate speech or discrimination' },
    { value: 'violence', label: 'Violence or dangerous content' },
    { value: 'inappropriate', label: 'Inappropriate or offensive content' },
    { value: 'copyright', label: 'Copyright infringement' },
    { value: 'misinformation', label: 'False information' },
    { value: 'other', label: 'Other (please specify)' }
  ];

  userReasons = [
    { value: 'harassment', label: 'Harassment or bullying' },
    { value: 'impersonation', label: 'Impersonation or fake account' },
    { value: 'hate-speech', label: 'Hate speech or discrimination' },
    { value: 'spam', label: 'Spam or bot activity' },
    { value: 'inappropriate', label: 'Inappropriate behavior' },
    { value: 'threats', label: 'Threats or violence' },
    { value: 'underage', label: 'Underage user (under 13)' },
    { value: 'other', label: 'Other (please specify)' }
  ];

  get reportReasons() {
    return this.data.type === 'POST' ? this.postReasons : this.userReasons;
  }

  get dialogTitle() {
    return this.data.type === 'POST' ? 'Report Post' : 'Report User';
  }

  get infoText() {
    return this.data.type === 'POST' ? "You're reporting:" : "You're reporting:";
  }

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
          const targetType = this.data.type === 'POST' ? 'post' : 'user';
          this.reportForm.patchValue({
            reason: `I am reporting this ${targetType} because it contains ${selectedReason.label.toLowerCase()}.`
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
        type: this.data.type,
        targetId: this.data.targetId,
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