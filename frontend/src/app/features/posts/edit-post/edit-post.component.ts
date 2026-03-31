import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PostService } from '../../../core/services/post.service';
import { CreatePostRequest, Post } from '../../../shared/models/post.model';


@Component({
  selector: 'app-edit-post',
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
  templateUrl: './edit-post.component.html',
  styleUrl: './edit-post.component.scss'
})
export class EditPostComponent implements OnInit {

  private fb = inject(FormBuilder);
  private postService = inject(PostService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  
  postForm: FormGroup;
  isLoading = signal(false);
  isLoadingPost = signal(true);
  errorMessage = signal<string | null>(null);
  selectedFile = signal<File | null>(null);
  previewUrl = signal<string | null>(null);
  fileType = signal<'image' | 'video' | null>(null);
  existingMediaUrl = signal<string | null>(null);
  postId: number | null = null;
  keepExistingMedia = signal(true);

  constructor() {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(300)]],
      content: ['', [Validators.required, Validators.maxLength(6000)]]
    });
  }


  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const postId = +params['id'];
      if (postId) {
        this.loadPost(postId);
        this.postId = postId;
        // this.loadComments(postId);
      }
    });
  }
//   ngOnInit(): void {
//      this.route.params.subscribe( params=>  'id' {
        
//      });
//     if (this.postId) {
//       this.loadPost(this.postId);
//     } else {
//       this.errorMessage.set('Post ID not found');
//       this.isLoadingPost.set(false);
//     }
//   }

  loadPost(id: number): void {
    this.isLoadingPost.set(true);
    this.postService.getPostById(id).subscribe({
      next: (post: Post) => {
        this.postForm.patchValue({
          title: post.title,
          content: post.content
        });
        
        if (post.mediaUrl) {
          // Point the preview to the existing server URL
          this.previewUrl.set(this.postService.getMediaUrl(post.mediaUrl));
          this.fileType.set(this.getMediaType(post.mediaUrl));
        }
        this.isLoadingPost.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load post');
        this.isLoadingPost.set(false);
      }
    });
  }

  getMediaType(url: string): 'image' | 'video' | null {
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];
    const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov'];
    
    const lowerUrl = url.toLowerCase();
    if (imageExtensions.some(ext => lowerUrl.includes(ext))) {
      return 'image';
    }
    if (videoExtensions.some(ext => lowerUrl.includes(ext))) {
      return 'video';
    }
    return 'image'; 
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
        this.errorMessage.set('Please select an image or video file');
        return;
      }
      
      if (file.size > 20 * 1024 * 1024) { 
        this.errorMessage.set("The file shouldn't be more than 20MB");
        return;
      }
      
      this.selectedFile.set(file);
      this.fileType.set(file.type.startsWith("image/") ? 'image' : 'video');
      this.keepExistingMedia.set(false);
      
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
    this.keepExistingMedia.set(false);
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    if (!this.postId) {
      this.errorMessage.set('Post ID not found');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    
    const postData: CreatePostRequest = {
      title: this.postForm.value.title,
      content: this.postForm.value.content,
      media: this.selectedFile() || undefined
    };

    this.postService.updatePost(this.postId, postData).subscribe({
      next: (response) => {
        console.log('Post updated successfully', response);
        this.isLoading.set(false);
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.log("Post can't be updated:", error);
        this.isLoading.set(false);
        this.errorMessage.set('Failed to update post. Try again later.');
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/home']);
  }

  get title() { 
    return this.postForm.get('title'); 
  }
  
  get content() { 
    return this.postForm.get('content'); 
  }
}