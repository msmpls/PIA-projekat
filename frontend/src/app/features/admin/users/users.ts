import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AdminUserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/users';
  private backendUrl = 'http://localhost:8080';

  loading = false;
  errorMessage = '';
  users = signal<AdminUserResponse[]>([]);
  filteredUsers = signal<AdminUserResponse[]>([]);
  searchTerm = '';
  statusFilter = '';

  statusOptions = ['PENDING', 'APPROVED', 'REJECTED'];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';

    this.http.get<AdminUserResponse[]>(this.apiUrl).subscribe({
      next: users => {
        this.users.set(users);
        this.applyFilter();
        this.loading = false;
      },
      error: error => {
        this.handleError(error, 'Greška pri učitavanju korisnika.');
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    const search = this.searchTerm.trim().toLowerCase();
    const status = this.statusFilter;

    const filtered = this.users().filter(user => {
      const text = `${user.username} ${user.first_name} ${user.last_name} ${user.email}`.toLowerCase();
      const matchesSearch = !search || text.includes(search);
      const matchesStatus = !status || user.status === status;
      return matchesSearch && matchesStatus;
    });

    this.filteredUsers.set(filtered);
  }

  updateStatus(user: AdminUserResponse, newStatus: string): void {
    if (!newStatus || newStatus === user.status) {
      return;
    }

    const previousStatus = user.status;
    user.status = newStatus;
    this.http.put(`${this.apiUrl}/${user.id}/status`, null, { params: { status: newStatus }, responseType: 'text' }).subscribe({
      next: () => {
        this.applyFilter();
      },
      error: error => {
        user.status = previousStatus;
        this.handleError(error, 'Greška pri ažuriranju statusa korisnika.');
      }
    });
  }

  confirmDelete(user: AdminUserResponse): void {
    const confirmed = window.confirm(`Da li želite da obrišete korisnika ${user.username} (${user.email})?`);
    if (!confirmed) {
      return;
    }

    this.http.delete(`${this.apiUrl}/${user.id}`, { responseType: 'text' }).subscribe({
      next: () => {
        this.users.set(this.users().filter(item => item.id !== user.id));
        this.applyFilter();
      },
      error: error => {
        this.handleError(error, 'Greška pri brisanju korisnika.');
      }
    });
  }

  buildProfileImageUrl(path: string): string {
    if (!path || !path.trim()) {
      return '';
    }
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path;
    }
    return `${this.backendUrl}${path.startsWith('/') ? '' : '/'}${path}`;
  }

  onProfileImageError(user: AdminUserResponse): void {
    user.profile_picture_path = '';
  }

  private handleError(error: HttpErrorResponse, fallbackMessage: string): void {
    this.errorMessage = error.error?.message || error.message || fallbackMessage;
  }
}
