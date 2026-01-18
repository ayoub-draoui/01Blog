import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PostService } from '../../../core/services/post.service';
import { CreatePostRequest } from '../../../shared/models/post.model';


@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.scss'
})
export class CreatePostComponent {
  postForm: FormGroup
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  selectedFile = signal<File | null>(null);
  previewUrl = signal<string | null>(null);
  fileType = signal<'image' | 'video' | null>(null);

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private router: Router
  ) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(300)]],
      content: ['', [Validators.required, Validators.maxLength(6000)]]
    });
  }
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files&& input.files[0]){
        const file = input.files[0];
        if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
        this.errorMessage.set('Please select an image or video file');
        return;
      }
      if (file.size > 20 * 1024 * 1024) {
        this.errorMessage.set("the file shouln't be more than 20MB");
        return;
      }
      this.selectedFile.set(file);
      this.fileType.set(file.type.startsWith("image/")? 'image': 'video');
       
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewUrl.set(e.target?.result as string);
      };
      reader.readAsDataURL(file);
       this.errorMessage.set(null);
    }

  } 
  
  
  removeFile(): void {
    this.selectedFile.set(null);
    this.previewUrl.set(null);
    this.fileType.set(null);
  }

  onSubmit(): void {
    if (this.postForm.invalid){
        this.postForm.markAllAsTouched();
        return
    }
    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    const postData: CreatePostRequest = {
      title: this.postForm.value.title,
      content: this.postForm.value.content,
      media: this.selectedFile() || undefined
    };
    this.postService.createPost(postData).subscribe({
        next: (responce) => {
            console.log('poste created a jomi', responce);
            this.isLoading.set(false);
            this.router.navigate(['/home']);
        },
        error: (error) => {
            console.log("post can't be created man!!",error);
            this.isLoading.set(false);
            this.errorMessage.set(
                error.error?.message || 'try again later'
            );
        }
    });
  }
   cancel(): void {
    this.router.navigate(['/home']);
  }
  get title() { return this.postForm.get('title'); }
  get content() { return this.postForm.get('content'); }




}