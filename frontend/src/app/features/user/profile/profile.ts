import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ProfileService } from './profile.service';
import { AuthService } from '../../../core/auth/auth.service';
import { AdminUserResponse } from '../../../core/models/user.model';
import { ReservationResponse } from '../../../core/models/reservation.model';
import { TrainingResponse } from '../../../core/models/training.model';
import { OrderResponse } from '../../../core/models/shop.model';

type SortKey = 'facility_name' | 'city' | 'court_name_number' | 'sport' | 'reservation_date' | 'start_time' | 'status';
type SortDir = 'asc' | 'desc';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  private fb = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private authService = inject(AuthService);

  backendUrl = 'http://localhost:8080';
  profile = signal<AdminUserResponse | null>(null);
  reservations = signal<ReservationResponse[]>([]);
  trainings = signal<TrainingResponse[]>([]);
  orders = signal<OrderResponse[]>([]);
  loading = signal(true);
  activeTab = signal<'info' | 'reservations' | 'trainings' | 'orders'>('info');
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  // Athlete-only: hide reservations/trainings/orders tabs for employees
  isAthlete = this.authService.getRole() === 'ATHLETE';

  // Edit mode
  editing = signal(false);
  editForm = this.fb.nonNullable.group({
    first_name: [''],
    last_name: [''],
    phone: [''],
    email: [''],
  });

  // Sports
  sportsInput = signal('');
  editingSports = signal(false);

  // Image
  selectedFile = signal<File | null>(null);
  imagePreview = signal<string>('');

  // Sorting reservations
  sortKey = signal<SortKey>('reservation_date');
  sortDir = signal<SortDir>('desc');

  sortedReservations = computed(() => {
    const data = [...this.reservations()];
    const key = this.sortKey();
    const dir = this.sortDir() === 'asc' ? 1 : -1;
    return data.sort((a, b) => {
      const valA = (a[key] ?? '').toString().toLowerCase();
      const valB = (b[key] ?? '').toString().toLowerCase();
      if (valA < valB) return -1 * dir;
      if (valA > valB) return 1 * dir;
      return 0;
    });
  });

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading.set(true);
    this.profileService.getProfile().subscribe({
      next: (data) => {
        this.profile.set(data);
        this.editForm.patchValue({
          first_name: data.first_name,
          last_name: data.last_name,
          phone: data.phone,
          email: data.email,
        });
        this.sportsInput.set(data.favorite_sports || '');
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.showMessage('error', 'Greška pri učitavanju profila.');
      },
    });
    // Athlete-only data: only load for athletes
    if (this.isAthlete) {
      this.loadReservations();
      this.loadTrainings();
      this.loadOrders();
    }
  }

  loadReservations(): void {
    this.profileService.getMyReservations().subscribe({
      next: (data) => this.reservations.set(data),
      error: () => {},
    });
  }

  loadTrainings(): void {
    this.profileService.getMyTrainings().subscribe({
      next: (data) => this.trainings.set(data),
      error: () => {},
    });
  }

  loadOrders(): void {
    this.profileService.getMyOrders().subscribe({
      next: (data) => this.orders.set(data),
      error: () => {},
    });
  }

  // ===== Profile editing =====
  startEdit(): void {
    this.editing.set(true);
  }

  cancelEdit(): void {
    this.editing.set(false);
    const p = this.profile();
    if (p) {
      this.editForm.patchValue({
        first_name: p.first_name,
        last_name: p.last_name,
        phone: p.phone,
        email: p.email,
      });
    }
  }

  saveEdit(): void {
    if (this.editForm.invalid) return;
    this.profileService.updateProfile(this.editForm.getRawValue()).subscribe({
      next: () => {
        this.editing.set(false);
        this.loadAll();
        this.showMessage('success', 'Podaci su uspešno ažurirani.');
      },
      error: () => this.showMessage('error', 'Greška pri ažuriranju.'),
    });
  }

  // ===== Sports editing =====
  saveSports(): void {
    this.profileService.updateSports(this.sportsInput()).subscribe({
      next: () => {
        this.editingSports.set(false);
        this.loadAll();
        this.showMessage('success', 'Omiljeni sportovi su ažurirani.');
      },
      error: () => this.showMessage('error', 'Greška pri ažuriranju sportova.'),
    });
  }

  // ===== Image upload =====
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile.set(file);
      const reader = new FileReader();
      reader.onload = () => this.imagePreview.set(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  uploadImage(): void {
    const file = this.selectedFile();
    if (!file) return;
    this.profileService.updateImage(file).subscribe({
      next: (res) => {
        this.selectedFile.set(null);
        this.imagePreview.set('');
        this.loadAll();
        this.showMessage('success', 'Profilna slika je ažurirana.');
      },
      error: () => this.showMessage('error', 'Greška pri otpremanju slike.'),
    });
  }

  getProfileImageUrl(): string {
    const p = this.profile();
    if (this.imagePreview()) return this.imagePreview();
    if (p?.profile_picture_path) return this.backendUrl + p.profile_picture_path;
    return '';
  }

  // ===== Reservation cancel =====
  canCancelReservation(res: ReservationResponse): boolean {
    // PENDING can always be cancelled
    if (res.status === 'PENDING') return true;
    // CANCELLED/COMPLETED cannot be cancelled
    if (res.status === 'CANCELLED' || res.status === 'COMPLETED') return false;
    // CONFIRMED: only if 12+ hours until start
    const start = new Date(`${res.reservation_date}T${res.start_time}`);
    const now = new Date();
    const diffHours = (start.getTime() - now.getTime()) / (1000 * 60 * 60);
    return diffHours >= 12;
  }

  cancelReservation(id: number): void {
    if (!confirm('Da li ste sigurni da želite da otkažete ovu rezervaciju?')) return;
    this.profileService.cancelReservation(id).subscribe({
      next: () => {
        this.loadReservations();
        this.showMessage('success', 'Rezervacija je otkazana.');
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri otkazivanju.');
      },
    });
  }

  // ===== Order cancel =====
  cancelOrder(id: number): void {
    if (!confirm('Da li ste sigurni da želite da otkažete ovu porudžbinu?')) return;
    this.profileService.cancelOrder(id).subscribe({
      next: () => {
        this.loadOrders();
        this.showMessage('success', 'Porudžbina je otkazana.');
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri otkazivanju.');
      },
    });
  }

  // ===== Sorting =====
  toggleSort(key: SortKey): void {
    if (this.sortKey() === key) {
      this.sortDir.set(this.sortDir() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortKey.set(key);
      this.sortDir.set('asc');
    }
  }

  sortIcon(key: SortKey): string {
    if (this.sortKey() !== key) return '↕';
    return this.sortDir() === 'asc' ? '↑' : '↓';
  }

  setTab(tab: 'info' | 'reservations' | 'trainings' | 'orders'): void {
    this.activeTab.set(tab);
  }

  formatTime(t: string): string {
    if (!t) return '';
    return t.length >= 5 ? t.substring(0, 5) : t;
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
