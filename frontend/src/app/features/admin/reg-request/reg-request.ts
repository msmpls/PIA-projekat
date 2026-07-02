import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AdminUserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-reg-request',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reg-request.html',
  styleUrl: './reg-request.css',
})
export class RegRequest implements OnInit {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/users';

  loading = false;
  errorMessage = '';
  pendingUsers = signal<AdminUserResponse[]>([]);

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
    this.loading = true;
    this.errorMessage = '';
    this.http.get<AdminUserResponse[]>(this.apiUrl).subscribe({
      next: users => {
        const pending = users.filter(u => u.status === 'PENDING');
        this.pendingUsers.set(pending);
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.error?.message || err.message || 'Greška pri učitavanju zahteva.';
        this.loading = false;
      }
    });
  }

  respond(userId: number, status: 'APPROVED' | 'REJECTED') {
    this.errorMessage = '';
    this.http.put(`${this.apiUrl}/${userId}/status`, null, { params: { status } , responseType: 'text'}).subscribe({
      next: () => {
        this.pendingUsers.set(this.pendingUsers().filter(u => u.id !== userId));
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.error?.message || err.message || 'Greška pri ažuriranju statusa.';
      }
    });
  }
}
