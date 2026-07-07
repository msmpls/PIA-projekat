import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FacilityService } from './facility.service';
import { EmployeeProfileResponse } from '../../../core/models/user.model';
import { CourtRequest, FacilityRequest } from '../../../core/models/facility.model';

interface CourtFormRow {
  court_name: string;
  sport_type: string;
  type: string;
  capacity: number;
  equipment_description: string;
}

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
  submitting = signal(false);

  // ===== Facility creation form =====
  facilityForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    city: ['', Validators.required],
    address: ['', Validators.required],
    description: [''],
    price_per_hour: [1000, [Validators.required, Validators.min(1)]],
    type: ['CLOSED', Validators.required],
    work_start_time: ['08:00', Validators.required],
    work_end_time: ['22:00', Validators.required],
    max_no_shows: [3, [Validators.required, Validators.min(1), Validators.max(10)]],
  });

  // Dynamic courts list for facility creation
  courts = signal<CourtFormRow[]>([
    { court_name: '', sport_type: '', type: 'OPENED', capacity: 4, equipment_description: '' },
  ]);

  // ===== Add court to existing facility form =====
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

  // ===== JSON file upload =====
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
        const input = document.querySelector<HTMLInputElement>('#jsonFileInput');
        if (input) input.value = '';
        this.refresh();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri dodavanju objekta.');
      },
    });
  }

  // ===== Facility creation from form =====
  addCourtRow(): void {
    this.courts.update(rows => [
      ...rows,
      { court_name: '', sport_type: '', type: 'CLOSED', capacity: 4, equipment_description: '' },
    ]);
  }

  removeCourtRow(index: number): void {
    if (this.courts().length <= 1) {
      this.showMessage('error', 'Morate imati barem jedan teren.');
      return;
    }
    this.courts.update(rows => rows.filter((_, i) => i !== index));
  }

  updateCourtField(index: number, field: keyof CourtFormRow, value: string | number): void {
    this.courts.update(rows => {
      const updated = [...rows];
      let val = value;
      if (field === 'capacity') {
        val = parseInt(String(value)) || 0;
      }
      updated[index] = { ...updated[index], [field]: val };
      return updated;
    });
  }

  getCompanyInfo(): { name: string; address: string; mb: string; pib: string } | null {
    const p = this.profile();
    if (!p?.myFacilities || p.myFacilities.length === 0) return null;
    const f = p.myFacilities[0];
    return {
      name: f.companyName || '—',
      address: f.companyAddress || '—',
      mb: f.companyIdNumber || '—',
      pib: f.pib || '—',
    };
  }

  createFacilityFromForm(): void {
    if (this.facilityForm.invalid) {
      this.facilityForm.markAllAsTouched();
      this.showMessage('error', 'Popunite sva obavezna polja objekta.');
      return;
    }

    const courtRows = this.courts();
    for (let i = 0; i < courtRows.length; i++) {
      const c = courtRows[i];
      if (!c.court_name || !c.sport_type || c.capacity < 1) {
        this.showMessage('error', `Teren #${i + 1}: popunite naziv, sport i kapacitet.`);
        return;
      }
      if (c.equipment_description.length > 300) {
        this.showMessage('error', `Teren #${i + 1}: opis opreme ne sme prelaziti 300 karaktera.`);
        return;
      }
    }

    const hasValidOpen = courtRows.some(c => c.type === 'OPENED' && c.capacity >= 4);
    if (!hasValidOpen) {
      this.showMessage('error', 'Objekat mora imati barem jedan otvoreni teren sa kapacitetom 4+.');
      return;
    }

    const company = this.getCompanyInfo();
    if (!company) {
      this.showMessage('error', 'Greška: nemate postojeći objekat za preuzimanje podataka o firmi.');
      return;
    }

    const fv = this.facilityForm.getRawValue();
    const request: FacilityRequest = {
      name: fv.name,
      city: fv.city,
      address: fv.address,
      description: fv.description,
      price_per_hour: Number(fv.price_per_hour),
      type: fv.type,
      work_start_time: fv.work_start_time,
      work_end_time: fv.work_end_time,
      max_no_shows: Number(fv.max_no_shows),
      company_name: '',
      company_address: '',
      company_id_number: '',
      pib: '',
      courts: courtRows.map(c => ({
        court_name: c.court_name,
        sport_type: c.sport_type,
        type: c.type as 'OPENED' | 'CLOSED',
        capacity: Number(c.capacity),
        equipment_description: c.equipment_description,
      })),
    };

    this.submitting.set(true);
    this.facilityService.createFacilityFromForm(request).subscribe({
      next: () => {
        this.submitting.set(false);
        this.showMessage('success', 'Objekat je kreiran i poslat na odobrenje administratora.');
        this.facilityForm.reset({
          name: '', city: '', address: '', description: '',
          price_per_hour: 1000, type: 'CLOSED',
          work_start_time: '08:00', work_end_time: '22:00',
          max_no_shows: 3,
        });
        this.courts.set([{ court_name: '', sport_type: '', type: 'OPENED', capacity: 4, equipment_description: '' }]);
        this.refresh();
      },
      error: (err) => {
        this.submitting.set(false);
        this.showMessage('error', err.error || 'Greška pri kreiranju objekta.');
      },
    });
  }

  // ===== Add court to existing facility =====
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

  getCourtEquipmentLength(index: number): number {
    return this.courts()[index]?.equipment_description?.length || 0;
  }

  hasExistingFacility(): boolean {
    const p = this.profile();
    return !!(p?.myFacilities && p.myFacilities.length > 0);
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
    trackByIndex(index: number): number {
    return index;
  }
}