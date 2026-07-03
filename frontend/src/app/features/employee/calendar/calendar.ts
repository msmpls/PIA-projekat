import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarService } from './calendar.service';
import { EmployeeProfileResponse, EmployeeFacilityDTO } from '../../../core/models/user.model';
import { Court } from '../../../core/models/court.model';
import { ReservationViewResponse } from '../../../core/models/reservation.model';

interface WeekDay {
  date: Date;
  dateStr: string;
  label: string;
  isToday: boolean;
}

@Component({
  selector: 'app-employee-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './calendar.html',
  styleUrl: './calendar.css',
})
export class Calendar implements OnInit {
  private calendarService = inject(CalendarService);

  // State signals
  profile = signal<EmployeeProfileResponse | null>(null);
  facilities = signal<EmployeeFacilityDTO[]>([]);
  courts = signal<Court[]>([]);
  selectedFacilityId = signal<number | null>(null);
  selectedCourtId = signal<number | null>(null);
  calendarData = signal<ReservationViewResponse[]>([]);
  weekOffset = signal(0);
  loading = signal(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);
  draggedReservationId = signal<number | null>(null);
  dragOverSlot = signal<{ dateStr: string; hour: number } | null>(null);

  // Computed: currently selected court (for type check)
  currentCourt = computed<Court | null>(() => {
    const id = this.selectedCourtId();
    return this.courts().find(c => c.id === id) ?? null;
  });

  // Computed: drag-and-drop is allowed only on CLOSED courts/halls
  isCourtClosed = computed(() => this.currentCourt()?.type === 'CLOSED');

  // Computed: 7 days starting Monday of the current week + offset
  weekDays = computed<WeekDay[]>(() => {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0=Sun, 1=Mon...
    const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    const monday = new Date(today);
    monday.setDate(today.getDate() + diffToMonday + this.weekOffset() * 7);

    const days: WeekDay[] = [];
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

  // Computed: hours 8..21 (each slot = hour to hour+1, last is 21:00-22:00)
  hours = computed<number[]>(() => {
    const arr: number[] = [];
    for (let h = 8; h < 22; h++) arr.push(h);
    return arr;
  });

  ngOnInit(): void {
    this.loading.set(true);
    this.calendarService.getEmployeeProfile().subscribe({
      next: (data) => {
        this.profile.set(data);
        this.facilities.set(data.myFacilities || []);
        if (this.facilities().length > 0) {
          const first = this.facilities()[0];
          this.selectedFacilityId.set(first.id);
          this.loadCourts(first.id);
        } else {
          this.loading.set(false);
        }
      },
      error: () => {
        this.loading.set(false);
        this.showMessage('error', 'Greška pri učitavanju profila.');
      },
    });
  }

  loadCourts(facilityId: number): void {
    this.calendarService.getFacilityDetails(facilityId).subscribe({
      next: (data) => {
        this.courts.set(data.courts || []);
        if (this.courts().length > 0) {
          this.selectedCourtId.set(this.courts()[0].id);
          this.loadCalendar();
        } else {
          this.selectedCourtId.set(null);
          this.calendarData.set([]);
          this.loading.set(false);
        }
      },
      error: () => {
        this.loading.set(false);
        this.showMessage('error', 'Greška pri učitavanju terena.');
      },
    });
  }

  onFacilityChange(): void {
    const fid = this.selectedFacilityId();
    this.courts.set([]);
    this.selectedCourtId.set(null);
    this.calendarData.set([]);
    if (fid !== null) {
      this.loadCourts(fid);
    }
  }

  onCourtChange(): void {
    this.calendarData.set([]);
    this.loadCalendar();
  }

  prevWeek(): void {
    this.weekOffset.update(v => v - 1);
    this.loadCalendar();
  }

  nextWeek(): void {
    this.weekOffset.update(v => v + 1);
    this.loadCalendar();
  }

  loadCalendar(): void {
    const courtId = this.selectedCourtId();
    const days = this.weekDays();
    if (courtId === null || days.length === 0) return;
    this.loading.set(true);
    this.calendarService
      .getCalendarData(courtId, days[0].dateStr, days[6].dateStr)
      .subscribe({
        next: (data) => {
          this.calendarData.set(data || []);
          this.loading.set(false);
        },
        error: () => {
          this.calendarData.set([]);
          this.loading.set(false);
          this.showMessage('error', 'Greška pri učitavanju kalendara.');
        },
      });
  }

  // Returns all items (RESERVATION or TRAINING) that START at this hour on this date
  getItemsForSlot(dateStr: string, hour: number): ReservationViewResponse[] {
    return this.calendarData().filter(item => {
      if (item.date !== dateStr) return false;
      const startHour = parseInt(item.startTime.split(':')[0], 10);
      return startHour === hour;
    });
  }

  // Draggable only if RESERVATION type and court is CLOSED
  isDraggable(item: ReservationViewResponse): boolean {
    return item.type === 'RESERVATION' && this.isCourtClosed();
  }

  onDragStart(event: DragEvent, item: ReservationViewResponse): void {
    if (!this.isDraggable(item)) {
      event.preventDefault();
      return;
    }
    this.draggedReservationId.set(item.id);
    if (event.dataTransfer) {
      event.dataTransfer.setData('text/plain', String(item.id));
      event.dataTransfer.effectAllowed = 'move';
    }
  }

  onDragEnd(): void {
    this.draggedReservationId.set(null);
    this.dragOverSlot.set(null);
  }

  // MUST call preventDefault on dragover to allow drop
  onDragOver(event: DragEvent, dateStr: string, hour: number): void {
    if (this.draggedReservationId() === null) return;
    event.preventDefault();
    if (event.dataTransfer) event.dataTransfer.dropEffect = 'move';
    const cur = this.dragOverSlot();
    if (!cur || cur.dateStr !== dateStr || cur.hour !== hour) {
      this.dragOverSlot.set({ dateStr, hour });
    }
  }

  onDragLeave(dateStr: string, hour: number): void {
    const cur = this.dragOverSlot();
    if (cur && cur.dateStr === dateStr && cur.hour === hour) {
      this.dragOverSlot.set(null);
    }
  }

  onDrop(event: DragEvent, dateStr: string, hour: number): void {
    event.preventDefault();
    event.stopPropagation();
    const id = this.draggedReservationId();
    this.dragOverSlot.set(null);
    this.draggedReservationId.set(null);
    if (id === null) return;

    const newStartTime = hour.toString().padStart(2, '0') + ':00:00';
    const newEndTime = (hour + 1).toString().padStart(2, '0') + ':00:00';

    this.loading.set(true);
    this.calendarService.moveReservation(id, dateStr, newStartTime, newEndTime).subscribe({
      next: () => {
        this.showMessage('success', 'Rezervacija je uspešno pomerena.');
        this.loadCalendar();
      },
      error: (err) => {
        this.loading.set(false);
        const txt = typeof err.error === 'string' ? err.error : 'Greška pri pomeranju rezervacije.';
        this.showMessage('error', txt);
      },
    });
  }

  isDragOver(dateStr: string, hour: number): boolean {
    const cur = this.dragOverSlot();
    return cur !== null && cur.dateStr === dateStr && cur.hour === hour;
  }

  currentWeekLabel(): string {
    const days = this.weekDays();
    if (days.length === 0) return '';
    const first = days[0].date;
    const last = days[6].date;
    return `${first.getDate()}.${first.getMonth() + 1}. – ${last.getDate()}.${last.getMonth() + 1}.${last.getFullYear()}`;
  }

  hourLabel(h: number): string {
    return h.toString().padStart(2, '0') + ':00';
  }

  formatTime(t: string): string {
    if (!t) return '';
    return t.length >= 5 ? t.substring(0, 5) : t;
  }

  private formatDate(d: Date): string {
    const y = d.getFullYear();
    const m = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
