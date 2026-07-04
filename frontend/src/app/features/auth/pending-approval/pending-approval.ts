import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-pending-approval',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pending-approval.html',
  styleUrl: './pending-approval.css',
})
export class PendingApproval {
  private authService = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);

  username = signal<string>(this.authService.currentUser()?.username ?? '');
  checking = signal(false);
  message = signal<{ type: 'success' | 'error' | 'info'; text: string } | null>(null);

  checkStatus(): void {
    if (!this.username()) {
      this.message.set({ type: 'error', text: 'Niste prijavljeni. Prijavite se da proverite status.' });
      return;
    }
    this.checking.set(true);
    this.message.set(null);

    // Koristimo login endpoint da proverimo da li je nalog odobren
    this.http.get<any>('http://localhost:8080/api/users/profile').subscribe({
      next: () => {
        // Ako prodje, nalog je odobren i token je validan
        this.checking.set(false);
        this.message.set({
          type: 'success',
          text: 'Vaš nalog je odobren! Preusmeravamo vas na prijavu...'
        });
        // Odjavimo trenutni (PENDING) token
        this.authService.logout();
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.checking.set(false);
        if (err.status === 401 || err.status === 403) {
          this.message.set({
            type: 'info',
            text: 'Vaš nalog još uvek čeka odobrenje administratora. Molimo vas da budete strpljivi.'
          });
        } else {
          this.message.set({
            type: 'error',
            text: 'Greška pri proveri statusa. Pokušajte ponovo.'
          });
        }
      },
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
