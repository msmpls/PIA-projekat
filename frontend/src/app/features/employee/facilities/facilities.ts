import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FacilityService } from './facility.service';
import { EmployeeProfileResponse } from '../../../core/models/user.model';
import { CourtRequest } from '../../../core/models/facility.model';

@Component({
  selector: 'app-employee-facilities',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './facilities.html',
  styleUrl: './facilities.css',
})
export class Facilities implements OnInit {
  private facilityService = inject(FacilityService);
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);

  profile = signal<EmployeeProfileResponse | null>(null);
  loading = signal<boolean>(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);
  selectedFacilityId = signal<number | null>(null);
  sports = signal<string[]>([]);
  selectedJsonFile: File | null = null;

  courtForm = this.fb.nonNullable.group({
    court_name: ['', Validators.required],
    sport_type: ['', Validators.required],
    type: ['OPENED', Validators.required],
    capacity: [1, [Validators.required, Validators.min(1)]],
    equipment_description: ['', [Validators.required, Validators.maxLength(300)]],
  });

  ngOnInit(): void {
    this.loadProfile();
    this.loadSports();
  }

  loadProfile(): void {
    this.loading.set(true);
    this.facilityService.getEmployeeProfile().subscribe({
      next: (data) => {
        this.profile.set(data);
        this.loading.set(false);
        // Auto-select first facility if any
        if (data.myFacilities && data.myFacilities.length > 0) {
          this.selectedFacilityId.set(data.myFacilities[0].id);
        }
      },
      error: () => {
        this.loading.set(false);
        this.showMessage('error', 'Greška pri učitavanju profila.');
      },
    });
  }

  loadSports(): void {
    this.http.get<string[]>('http://localhost:8080/api/sports/names').subscribe({
      next: (data) => this.sports.set(data || []),
      error: () => this.sports.set([]),
    });
  }

  onJsonFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedJsonFile = input.files[0];
    } else {
      this.selectedJsonFile = null;
    }
  }

  uploadJson(): void {
    if (!this.selectedJsonFile) {
      this.showMessage('error', 'Izaberite JSON fajl prvo.');
      return;
    }
    this.facilityService.createFacilityFromJson(this.selectedJsonFile).subscribe({
      next: () => {
        this.showMessage('success', 'Objekat je uspešno dodat. Čeka odobrenje administratora.');
        this.selectedJsonFile = null;
        // reset file input
        const input = document.querySelector<HTMLInputElement>('#jsonFileInput');
        if (input) input.value = '';
        this.refresh();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri dodavanju objekta.');
      },
    });
  }

  onFacilityChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const id = Number(select.value);
    this.selectedFacilityId.set(id || null);
  }

  addCourt(): void {
    if (this.courtForm.invalid) {
      this.courtForm.markAllAsTouched();
      this.showMessage('error', 'Popunite sva obavezna polja.');
      return;
    }
    const facilityId = this.selectedFacilityId();
    if (!facilityId) {
      this.showMessage('error', 'Izaberite objekat kojem dodajete teren.');
      return;
    }
    const value = this.courtForm.getRawValue();
    const req: CourtRequest = {
      court_name: value.court_name,
      sport_type: value.sport_type,
      type: value.type as 'OPENED' | 'CLOSED',
      capacity: Number(value.capacity),
      equipment_description: value.equipment_description,
    };
    this.facilityService.addCourt(facilityId, req).subscribe({
      next: () => {
        this.showMessage('success', 'Teren je uspešno dodat.');
        this.courtForm.reset({
          court_name: '',
          sport_type: '',
          type: 'OPENED',
          capacity: 1,
          equipment_description: '',
        });
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri dodavanju terena.');
      },
    });
  }

  refresh(): void {
    this.loadProfile();
  }

  get equipmentLength(): number {
    return this.courtForm.controls.equipment_description.value.length;
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
