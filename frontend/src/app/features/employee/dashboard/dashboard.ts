import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationViewResponse } from '../../../core/models/reservation.model';
import { DashboardService } from './dashboard.service';

@Component({
  selector: 'app-employee-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private dashboardService = inject(DashboardService);

  checkInItems = signal<ReservationViewResponse[]>([]);
  pendingItems = signal<ReservationViewResponse[]>([]);
  loading = signal<boolean>(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.dashboardService.getCheckInTable().subscribe({
      next: (data) => this.checkInItems.set(data || []),
      error: () => this.checkInItems.set([]),
    });
    this.dashboardService.getPending().subscribe({
      next: (data) => this.pendingItems.set(data || []),
      error: () => this.pendingItems.set([]),
      complete: () => this.loading.set(false),
    });
  }

  confirmCheckIn(item: ReservationViewResponse): void {
    if (!item.actionAllowed) {
      this.showMessage('error', 'Akcija nije dozvoljena. Samo 10 min pre početka.');
      return;
    }
    this.dashboardService.checkIn(item.type, item.id, 'CONFIRM').subscribe({
      next: () => {
        this.showMessage('success', 'Korisnik uspešno potvrđen.');
        this.loadCheckIn();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri potvrdi.');
      },
    });
  }

  rejectCheckIn(item: ReservationViewResponse): void {
    if (!item.actionAllowed) {
      this.showMessage('error', 'Akcija nije dozvoljena. Samo 10 min pre početka.');
      return;
    }
    this.dashboardService.checkIn(item.type, item.id, 'REJECT').subscribe({
      next: () => {
        this.showMessage('success', 'Korisnik uspešno odjavljen.');
        this.loadCheckIn();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri odjavi.');
      },
    });
  }

  approvePending(item: ReservationViewResponse): void {
    this.dashboardService.decidePending(item.id, true).subscribe({
      next: () => {
        this.showMessage('success', 'Rezervacija odobrena.');
        this.loadPending();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri odobrenju.');
      },
    });
  }

  rejectPending(item: ReservationViewResponse): void {
    this.dashboardService.decidePending(item.id, false).subscribe({
      next: () => {
        this.showMessage('success', 'Rezervacija odbijena.');
        this.loadPending();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri odbijanju.');
      },
    });
  }

  loadCheckIn(): void {
    this.dashboardService.getCheckInTable().subscribe({
      next: (data) => this.checkInItems.set(data || []),
      error: () => this.checkInItems.set([]),
    });
  }

  loadPending(): void {
    this.dashboardService.getPending().subscribe({
      next: (data) => this.pendingItems.set(data || []),
      error: () => this.pendingItems.set([]),
    });
  }

  formatTime(time: string): string {
    if (!time) return '';
    return time.length >= 5 ? time.substring(0, 5) : time;
  }

  formatDate(date: string): string {
    if (!date) return '';
    const parts = date.split('-');
    if (parts.length === 3) {
      return `${parts[2]}.${parts[1]}.${parts[0]}`;
    }
    return date;
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
