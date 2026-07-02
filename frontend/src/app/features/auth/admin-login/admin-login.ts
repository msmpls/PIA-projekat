import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-admin-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './admin-login.html',
  styleUrl: './admin-login.css',
})
export class AdminLogin {
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
            this.errorMessage.set('Ova ruta je namenjena samo administratoru.');
          }
        },
        error: (err) => {
          this.errorMessage.set(err?.error?.message || 'Pogrešno korisničko ime ili lozinka.');
        }
      });
    }
  }
}
