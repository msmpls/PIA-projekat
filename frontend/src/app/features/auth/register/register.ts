import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // Fajl koji korisnik bira
  private selectedFile: File | null = null;
  errorMessage = signal<string>('');

// ... u register.component.ts

registerForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]], // Pazi na regex!
    first_name: ['', Validators.required],
    last_name: ['', Validators.required],
    role: ['ATHLETE', Validators.required], // <--- DODAJ OVO
    phone: ['0610222222']
});

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit() {
    if (this.registerForm.invalid) return;

    const formData = new FormData();
    const formValues = this.registerForm.value;

    // Dodajemo polja u FormData
    Object.keys(formValues).forEach(key => {
      formData.append(key, formValues[key as keyof typeof formValues]!);
    });

    // Dodajemo sliku ako postoji
    if (this.selectedFile) {
      formData.append('profile_image', this.selectedFile);
    }

    this.authService.register(formData).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => this.errorMessage.set('Došlo je do greške pri registraciji.')
    });
  }
}