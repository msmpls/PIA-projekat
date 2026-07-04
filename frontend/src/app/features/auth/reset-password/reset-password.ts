import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  token = signal<string>('');
  tokenValid = signal<boolean | null>(null); // null = checking, true = valid, false = invalid
  loading = signal(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  form = this.fb.nonNullable.group({
    newPassword: ['', [Validators.required, Validators.pattern(/^(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9])[a-zA-Z].{7,11}$/)]],
    confirmPassword: ['', Validators.required],
  });

  ngOnInit(): void {
    const t = this.route.snapshot.paramMap.get('token') || '';
    this.token.set(t);
    if (!t) {
      this.tokenValid.set(false);
      return;
    }
    this.verifyToken(t);
  }

  private verifyToken(t: string): void {
    this.http.get<any>(`http://localhost:8080/api/auth/verify-reset-token?token=${t}`).subscribe({
      next: () => this.tokenValid.set(true),
      error: () => this.tokenValid.set(false),
    });
  }

  submit(): void {
    if (this.form.invalid) return;

    const { newPassword, confirmPassword } = this.form.getRawValue();
    if (newPassword !== confirmPassword) {
      this.message.set({ type: 'error', text: 'Lozinke se ne poklapaju.' });
      return;
    }

    this.loading.set(true);
    this.http.post<any>('http://localhost:8080/api/auth/reset-password', {
      token: this.token(),
      newPassword,
    }).subscribe({
      next: () => {
        this.loading.set(false);
        this.message.set({ type: 'success', text: 'Lozinka je uspešno promenjena! Preusmeravamo vas na prijavu...' });
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: (err) => {
        this.loading.set(false);
        this.message.set({ type: 'error', text: err.error?.error || 'Greška pri promeni lozinke.' });
      },
    });
  }
}
