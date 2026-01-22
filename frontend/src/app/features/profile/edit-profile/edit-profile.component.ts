import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { PostService } from '../../../core/services/post.service';
import { UpdateProfileRequest } from '../../../shared/models/user.model';


@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.scss'
})

export class EditProfileComponent implements OnInit {
   private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private postService = inject(PostService);
  private snackBar = inject(MatSnackBar);

  isLoading = signal(false);
  isUploadingAvatar = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  selectedAvatarFile = signal<File | null>(null);
  avatarPreviewUrl = signal<string | null>(null);


   profileForm: FormGroup;
  passwordForm: FormGroup;

  currentUser = this.authService.currentUser;

   constructor() {
    this.profileForm = this.fb.group({
      firstname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      bio: ['', [Validators.maxLength(500)]],
      location: ['', [Validators.maxLength(100)]],
      website: ['', [Validators.maxLength(200)]]
    });
    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }
   
  
  
  ngOnInit(): void {
    this.loadCurrentUser();
  }



  loadCurrentUser(): void {
    this.authService.refreshCurrentUser().subscribe({
      next: (user) => {
        // Populate form with current user data
        this.profileForm.patchValue({
          firstname: user.firstname,
          lastname: user.lastname,
          bio: user.bio || '',
          location: user.location || '',
          website: user.website || ''
        });
        if (user.avatar) {
          this.avatarPreviewUrl.set(this.getMediaUrl(user.avatar));
        }
        this.isLoading.set(false)


  }, 
  error: (error) => {
        console.log("ana error have occured trying to bring the user ")
        this.errorMessage.set("sorry can't load the profile")
        this.isLoading.set(false)

  }

  })
}

onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement
    if (input.files && input.files[0]){
        const file = input.files[0]
            if (!file.type.startsWith('image/')) {
        this.snackBar.open('Please select an image file', 'Close', { duration: 3000 });
        return;
      }


       if (file.size > 5 * 1024 * 1024) {
        this.snackBar.open('Image size must not exceed 5MB', 'Close', { duration: 3000 });
        return;
      }

      this.selectedAvatarFile.set(file);

      const reader = new FileReader();
      reader.onload = (e) => {
        this.avatarPreviewUrl.set(e.target?.result as string)
      }

      reader.readAsDataURL(file);


    }


}

uploadAvatar(): void {
    const avatarFile = this.selectedAvatarFile();
    if (!avatarFile) {
      this.snackBar.open('Please select an avatar first', 'Close', { duration: 3000 });
      return;
    }

    this.isUploadingAvatar.set(true);

    this.userService.updateAvatar(avatarFile).subscribe({
      next: (updatedUser) => {
        this.isUploadingAvatar.set(false);
        this.selectedAvatarFile.set(null);
        this.avatarPreviewUrl.set(this.getMediaUrl(updatedUser.avatar));
        
        // Refresh auth service user
        this.authService.refreshCurrentUser().subscribe();
        
        this.snackBar.open('Avatar updated successfully!', 'Close', { duration: 3000 });
      },
      error: (error) => {
        console.error('Error uploading avatar:', error);
        this.isUploadingAvatar.set(false);
        this.snackBar.open('Failed to upload avatar', 'Close', { duration: 3000 });
      }
    });
  }



  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const profileData: UpdateProfileRequest = this.profileForm.value;

    this.userService.updateProfile(profileData).subscribe({
      next: (updatedUser) => {
        this.isLoading.set(false);
        this.successMessage.set('Profile updated successfully!');
        
        // Refresh auth service user
        this.authService.refreshCurrentUser().subscribe();
        
        this.snackBar.open('Profile updated!', 'Close', { duration: 3000 });
        
        // Navigate to profile after 1 second
        setTimeout(() => {
          this.router.navigate(['/profile', updatedUser.username]);
        }, 1000);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.isLoading.set(false);
        this.errorMessage.set(error.error?.message || 'Failed to update profile');
      }
    });
  }


  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);

    const passwordData = {
      currentPassword: this.passwordForm.value.currentPassword,
      newPassword: this.passwordForm.value.newPassword
    };

    // You'll need to add this method to UserService
    this.userService.changePassword(passwordData).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.passwordForm.reset();
        this.snackBar.open('Password changed successfully!', 'Close', { duration: 3000 });
      },
      error: (error) => {
        console.error('Error changing password:', error);
        this.isLoading.set(false);
        this.snackBar.open(
          error.error?.message || 'Failed to change password',
          'Close',
          { duration: 3000 }
        );
      }
    });
  }
  cancel(): void {
    const user = this.currentUser();
    if (user) {
      this.router.navigate(['/profile', user.username]);
    } else {
      this.router.navigate(['/home']);
    }
  }


  getMediaUrl(filename: string): string {
    return this.postService.getMediaUrl(filename);
  }

 removeAvatar(): void {
    this.selectedAvatarFile.set(null);
    this.avatarPreviewUrl.set(null);
  }

  passwordMatchValidator(form: any) {
    const newPassword = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');

    if (!newPassword || !confirmPassword) {
      return null;
    }

    return newPassword.value === confirmPassword.value ? null : { passwordMismatch: true };
  }




get firstname() { return this.profileForm.get('firstname'); }
  get lastname() { return this.profileForm.get('lastname'); }
  get bio() { return this.profileForm.get('bio'); }
  get location() { return this.profileForm.get('location'); }
  get website() { return this.profileForm.get('website'); }

  get currentPassword() { return this.passwordForm.get('currentPassword'); }
  get newPassword() { return this.passwordForm.get('newPassword'); }
  get confirmPassword() { return this.passwordForm.get('confirmPassword'); }




}