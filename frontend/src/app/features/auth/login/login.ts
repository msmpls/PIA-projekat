import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgIf],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  errorMessage = signal<string>('');

  form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  submit() {
    if (this.form.valid) {
      this.authService.login(this.form.getRawValue()).subscribe({
        next: (response) => {
          if (response.role === 'ADMIN') {
            this.router.navigate(['/admin/facility-requests']);
          } else {
            this.router.navigate(['/profile']);
          }
        },
        error: (err) => {
          this.errorMessage.set(err?.error?.message || 'Pogrešno korisničko ime ili lozinka.');
        }
      });
    }
  }
}
