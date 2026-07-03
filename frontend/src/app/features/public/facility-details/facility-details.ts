import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FacilityDetails as FacilityDetailsData } from '../../../core/models/facility-details.model';
import { FacilityRatings } from '../../../shared/components/facility-ratings/facility-ratings';
import { SafeUrlPipe } from '../../../shared/pipes/safe-url.pipe';
import { AuthService } from '../../../core/auth/auth.service';
import { ReservationRequest } from '../../../core/models/reservation.model';

@Component({
  selector: 'app-facility-details',
  standalone: true,
  imports: [CommonModule, FacilityRatings, SafeUrlPipe],
  templateUrl: './facility-details.html',
  styleUrl: './facility-details.css',
})
export class FacilityDetails implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  private apiUrl = 'http://localhost:8080/api/public/facilities';
  private reservationsUrl = 'http://localhost:8080/api/reservations';

  details = signal<FacilityDetailsData | null>(null);
  loading = signal(true);
  error = signal('');
  selectedImage = signal<string>('');
  facilityId = 0;

  // Calendar state
  currentCourtIndex = signal(0);
  weekOffset = signal(0); // 0 = current week, -1 = previous, 1 = next
  occupiedSlots = signal<Map<string, string>>(new Map()); // key = "YYYY-MM-DD_HH:00", value = "CONFIRMED" or "PENDING"
  calendarLoading = signal(false);
  selectedSlot = signal<{ date: string; startHour: number } | null>(null);
  reservationMessage = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  // Auth
  isAthlete = this.authService.getRole() === 'ATHLETE';
  isLoggedIn = this.authService.isLoggedIn();

  // Computed: current court
  currentCourt = computed(() => {
    const courts = this.details()?.courts ?? [];
    return courts[this.currentCourtIndex()] ?? null;
  });

  // Computed: week days (7 days starting from Monday of current week + offset)
  weekDays = computed(() => {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0=Sun, 1=Mon...
    const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    const monday = new Date(today);
    monday.setDate(today.getDate() + diffToMonday + this.weekOffset() * 7);

    const days: { date: Date; dateStr: string; label: string; isToday: boolean }[] = [];
    const dayNames = ['Pon', 'Uto', 'Sre', 'Čet', 'Pet', 'Sub', 'Ned'];
    for (let i = 0; i < 7; i++) {
      const d = new Date(monday);
      d.setDate(monday.getDate() + i);
      const dateStr = this.formatDate(d);
      const isToday = this.formatDate(today) === dateStr;
      days.push({
        date: d,
        dateStr,
        label: `${dayNames[i]} ${d.getDate()}.${d.getMonth() + 1}.`,
        isToday,
      });
    }
    return days;
  });

  // Computed: hours range from facility work hours
  hours = computed(() => {
    const f = this.details()?.facility;
    if (!f) return [];
    const start = parseInt(f.work_start_time.split(':')[0]);
    const end = parseInt(f.work_end_time.split(':')[0]);
    const hours: number[] = [];
    for (let h = start; h < end; h++) hours.push(h);
    return hours;
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.facilityId = Number(idParam);
    if (!this.facilityId) {
      this.error.set('Neispravan ID objekta.');
      this.loading.set(false);
      return;
    }
    this.loadDetails();
  }

  loadDetails(): void {
    this.loading.set(true);
    this.http.get<FacilityDetailsData>(`${this.apiUrl}/${this.facilityId}`).subscribe({
      next: (data) => {
        this.details.set(data);
        if (data.galleryImages && data.galleryImages.length > 0) {
          this.selectedImage.set(data.galleryImages[0]);
        }
        this.loading.set(false);
        if (data.courts && data.courts.length > 0) {
          this.loadOccupiedSlots();
        }
      },
      error: () => {
        this.error.set('Objekat nije pronađen ili nije odobren.');
        this.loading.set(false);
      },
    });
  }

  // ===== Court rotation =====
  rotateCourtLeft(): void {
    const courts = this.details()?.courts ?? [];
    if (courts.length === 0) return;
    this.currentCourtIndex.set((this.currentCourtIndex() - 1 + courts.length) % courts.length);
    this.selectedSlot.set(null);
    this.loadOccupiedSlots();
  }

  rotateCourtRight(): void {
    const courts = this.details()?.courts ?? [];
    if (courts.length === 0) return;
    this.currentCourtIndex.set((this.currentCourtIndex() + 1) % courts.length);
    this.selectedSlot.set(null);
    this.loadOccupiedSlots();
  }

  selectCourt(index: number): void {
    this.currentCourtIndex.set(index);
    this.selectedSlot.set(null);
    this.loadOccupiedSlots();
  }

  // ===== Week navigation =====
  prevWeek(): void {
    this.weekOffset.update(v => v - 1);
    this.selectedSlot.set(null);
    this.loadOccupiedSlots();
  }

  nextWeek(): void {
    this.weekOffset.update(v => v + 1);
    this.selectedSlot.set(null);
    this.loadOccupiedSlots();
  }

  currentWeekLabel(): string {
    const days = this.weekDays();
    if (days.length === 0) return '';
    const first = days[0].date;
    const last = days[6].date;
    return `${first.getDate()}.${first.getMonth() + 1}. – ${last.getDate()}.${last.getMonth() + 1}.${last.getFullYear()}`;
  }

  // ===== Slot loading =====
  loadOccupiedSlots(): void {
    const court = this.currentCourt();
    if (!court) return;
    this.calendarLoading.set(true);
    const days = this.weekDays();
    const newMap = new Map<string, string>();

    let completed = 0;
    const total = days.length;

    for (const day of days) {
      this.http.get<any[]>(`${this.reservationsUrl}/court/${court.id}?date=${day.dateStr}`).subscribe({
        next: (reservations) => {
          for (const r of reservations) {
            const startHour = parseInt(r.start_time.split(':')[0]);
            const endHour = parseInt(r.end_time.split(':')[0]);
            for (let h = startHour; h < endHour; h++) {
              newMap.set(`${day.dateStr}_${h}:00`, r.status); // "CONFIRMED" or "PENDING"
            }
          }
          completed++;
          if (completed === total) {
            this.occupiedSlots.set(newMap);
            this.calendarLoading.set(false);
          }
        },
        error: () => {
          completed++;
          if (completed === total) {
            this.occupiedSlots.set(newMap);
            this.calendarLoading.set(false);
          }
        },
      });
    }
  }

  getSlotStatus(dateStr: string, hour: number): string | null {
    return this.occupiedSlots().get(`${dateStr}_${hour}:00`) ?? null;
  }

  isSlotOccupied(dateStr: string, hour: number): boolean {
    return this.occupiedSlots().has(`${dateStr}_${hour}:00`);
  }

  isSlotSelected(dateStr: string, hour: number): boolean {
    const sel = this.selectedSlot();
    return sel !== null && sel.date === dateStr && sel.startHour === hour;
  }

  isSlotPast(dateStr: string, hour: number): boolean {
    const slotDate = new Date(dateStr + 'T' + hour.toString().padStart(2, '0') + ':00:00');
    return slotDate.getTime() < Date.now();
  }

  clickSlot(dateStr: string, hour: number): void {
    if (!this.isAthlete) return;
    if (this.isSlotOccupied(dateStr, hour)) return;
    if (this.isSlotPast(dateStr, hour)) return;
    this.selectedSlot.set({ date: dateStr, startHour: hour });
    this.reservationMessage.set(null);
  }

  // ===== Reservation =====
  reserveSlot(): void {
    const sel = this.selectedSlot();
    const court = this.currentCourt();
    if (!sel || !court) return;

    const startTime = sel.startHour.toString().padStart(2, '0') + ':00:00';
    const endTime = (sel.startHour + 1).toString().padStart(2, '0') + ':00:00';

    const req: ReservationRequest = {
      facility_id: this.facilityId,
      court_id: court.id,
      reservation_date: sel.date,
      start_time: startTime,
      end_time: endTime,
    };

    this.http.post(`${this.reservationsUrl}`, req, { responseType: 'text' }).subscribe({
      next: () => {
        this.reservationMessage.set({ type: 'success', text: 'Rezervacija je uspešno poslata! Čeka odobrenje zaposlenog.' });
        this.selectedSlot.set(null);
        this.loadOccupiedSlots();
      },
      error: (err) => {
        this.reservationMessage.set({ type: 'error', text: err.error || 'Greška pri rezervaciji.' });
      },
    });
  }

  cancelSelection(): void {
    this.selectedSlot.set(null);
  }

  // ===== Map =====
  getMapUrl(): string {
    const f = this.details()?.facility;
    if (!f) return '';
    const query = encodeURIComponent(`${f.name}, ${f.address}, ${f.city}`);
    return `https://www.openstreetmap.org/export/embed.html?bbox=&marker=&q=${query}`;
  }

  // ===== Helpers =====
  selectImage(img: string): void {
    this.selectedImage.set(img);
  }

  goBack(): void {
    this.router.navigate(['/search']);
  }

  formatDate(d: Date): string {
    const y = d.getFullYear();
    const m = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  hourLabel(h: number): string {
    return h.toString().padStart(2, '0') + ':00';
  }
}
