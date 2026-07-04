import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);

  form = this.fb.nonNullable.group({
    identifier: ['', Validators.required],
  });

  loading = signal(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);
  resetToken = signal<string>('');

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.message.set(null);

    this.http.post<any>('http://localhost:8080/api/auth/forgot-password', this.form.value).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.resetToken.set(res.token || '');
        this.message.set({
          type: 'success',
          text: 'Reset link je generisan! Kliknite na link ispod da postavite novu lozinku.',
        });
      },
      error: () => {
        this.loading.set(false);
        this.message.set({ type: 'error', text: 'Greška pri slanju zahteva.' });
      },
    });
  }

  goToReset(): void {
    const token = this.resetToken();
    if (token) {
      this.router.navigate(['/reset-password', token]);
    }
  }
}
