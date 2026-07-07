import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TrainingService } from './training.service';
import { Coach, TrainingResponse } from '../../../core/models/training.model';
import { Facility } from '../../../core/models/facility.model';
import { FacilityDetails } from '../../../core/models/facility-details.model';
import { Court } from '../../../core/models/court.model';

@Component({
  selector: 'app-trainings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './trainings.html',
  styleUrl: './trainings.css',
})
export class Trainings implements OnInit {
  private fb = inject(FormBuilder);
  private trainingService = inject(TrainingService);
  private http = inject(HttpClient);

  private base = 'http://localhost:8080/api';

  facilities = signal<Facility[]>([]);
  sports = signal<string[]>([]);
  coaches = signal<Coach[]>([]);
  allCoaches = signal<Coach[]>([]);  // NOVO: svi trenere objekta (za preview)
  availableSports = signal<string[]>([]);  // NOVO: sportovi koji imaju trenere
  courts = signal<Court[]>([]);
  myTrainings = signal<TrainingResponse[]>([]);
  selectedFacility = signal<number | null>(null);
  selectedSport = signal<string>('');
  selectedCoachId = signal<number | null>(null);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);
  submitting = signal(false);

  today = new Date().toISOString().split('T')[0];

  form = this.fb.nonNullable.group({
    coach: [0, [Validators.required, Validators.min(1)]],
    court: [0, [Validators.required, Validators.min(1)]],
    training_date: ['', Validators.required],
    start_time: ['', Validators.required],
    end_time: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadFacilities();
    this.loadSports();
    this.loadMyTrainings();
  }

  loadFacilities(): void {
    this.http.get<Facility[]>(`${this.base}/public/facilities`).subscribe({
      next: (data) => this.facilities.set(data || []),
      error: () => this.showMessage('error', 'Greška pri učitavanju objekata.'),
    });
  }

  loadSports(): void {
    this.http.get<string[]>(`${this.base}/sports/names`).subscribe({
      next: (data) => this.sports.set(data || []),
      error: () => this.showMessage('error', 'Greška pri učitavanju sportova.'),
    });
  }

  loadMyTrainings(): void {
    this.trainingService.getMyTrainings().subscribe({
      next: (data) => this.myTrainings.set(data || []),
      error: () => {},
    });
  }

  onFacilityChange(): void {
    const fid = this.selectedFacility();
    // reset dependent state
    this.courts.set([]);
    this.coaches.set([]);
    this.allCoaches.set([]);
    this.availableSports.set([]);
    this.selectedCoachId.set(null);
    this.selectedSport.set('');
    this.form.patchValue({ coach: 0, court: 0 });

    if (fid) {
      // load courts
      this.http.get<FacilityDetails>(`${this.base}/public/facilities/${fid}`).subscribe({
        next: (data) => this.courts.set(data.courts || []),
        error: () => this.showMessage('error', 'Greška pri učitavanju terena.'),
      });
      // NOVO: load all coaches for preview
      this.trainingService.getAllCoachesByFacility(fid).subscribe({
        next: (data) => {
          this.allCoaches.set(data || []);
          // Izvuci unikatne sportove koji imaju trenere
          const sportSet = new Set((data || []).map(c => c.sport));
          this.availableSports.set(Array.from(sportSet).sort());
        },
        error: () => this.allCoaches.set([]),
      });
    }
  }

  onSportChange(): void {
    const fid = this.selectedFacility();
    const sport = this.selectedSport();
    // reset coaches
    this.coaches.set([]);
    this.selectedCoachId.set(null);
    this.form.patchValue({ coach: 0 });

    if (fid && sport) {
      this.loadCoaches(fid, sport);
    }
  }

  loadCoaches(facilityId: number, sport: string): void {
    this.trainingService.getCoaches(facilityId, sport).subscribe({
      next: (data) => this.coaches.set(data || []),
      error: () => this.showMessage('error', 'Greška pri učitavanju trenera.'),
    });
  }

   // Klik na coach preview karticu
  selectCoachFromCard(coach: Coach): void {
    // 1. Popuni sport signal i DOM
    this.selectedSport.set(coach.sport);
    const sportSelect = document.getElementById('sport') as HTMLSelectElement;
    if (sportSelect) {
      sportSelect.value = coach.sport;
    }

    // 2. Popuni trenera signal
    this.selectedCoachId.set(coach.id);

    // 3. Učitaj trenere za taj sport, pa ONDA popuni formu
    const fid = this.selectedFacility();
    if (fid) {
      this.trainingService.getCoaches(fid, coach.sport).subscribe({
        next: (data) => {
          this.coaches.set(data || []);
          // Koristi setTimeout da sačekaš Angular change detection
          setTimeout(() => {
            this.form.patchValue({ coach: coach.id });
            // Skroluj do forme
            document.querySelector('.booking-form')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
          }, 50);
        },
        error: () => {}
      });
    }
  }

  selectCoach(coach: Coach): void {
    this.selectedCoachId.set(coach.id);
    this.form.patchValue({ coach: coach.id });
  }

  bookTraining(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.showMessage('error', 'Popunite sva polja pre zakazivanje.');
      return;
    }
    const raw = this.form.getRawValue();
    const req = {
      coach_id: Number(raw.coach),
      court_id: Number(raw.court),
      training_date: raw.training_date,
      start_time: this.padTime(raw.start_time),
      end_time: this.padTime(raw.end_time),
    };
    this.submitting.set(true);
    this.trainingService.scheduleTraining(req).subscribe({
      next: () => {
        this.submitting.set(false);
        this.showMessage('success', 'Trening je uspešno zakazan!');
        this.form.reset({
          coach: 0,
          court: 0,
          training_date: '',
          start_time: '',
          end_time: '',
        });
        this.selectedCoachId.set(null);
        this.loadMyTrainings();
      },
      error: (err) => {
        this.submitting.set(false);
        const txt = typeof err.error === 'string' ? err.error : 'Greška pri zakazivanju treninga.';
        this.showMessage('error', txt);
      },
    });
  }

  private padTime(t: string): string {
    if (!t) return '';
    if (t.length === 5) return `${t}:00`;
    return t;
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'SCHEDULED':
        return 'Zakazano';
      case 'COMPLETED':
        return 'Završeno';
      case 'CANCELLED':
        return 'Otkazano';
      default:
        return status;
    }
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