import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { NgIf, NgFor } from '@angular/common';
import { Facility } from '../../../core/models/facility.model';
import { Court } from '../../../core/models/court.model';

@Component({
  selector: 'app-facility-request',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor],
  templateUrl: './facility-request.html',
  styleUrl: './facility-request.css',
})
export class FacilityRequest implements OnInit {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin/facilities/pending';

  loading = signal(false);
  errorMessage = signal('');
  facilities = signal<Facility[]>([]);

  selectedFacility = signal<Facility | null>(null);
  selectedCourts = signal<Court[]>([]);
  detailLoading = signal(false);
  detailErrorMessage = signal('');

  ngOnInit(): void {
    this.loadPendingFacilities();
  }

  loadPendingFacilities(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.http.get<Facility[]>(this.apiUrl).subscribe({
      next: facilities => {
        this.facilities.set(facilities);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage.set(err.error?.message || err.message || 'Greška pri učitavanju objekata.');
        this.loading.set(false);
      }
    });
  }

  respond(facilityId: number, status: 'APPROVED' | 'REJECTED'): void {
    this.errorMessage.set('');
    this.detailErrorMessage.set('');
    this.http.put(`http://localhost:8080/api/admin/facilities/${facilityId}/status`, null, {
      params: { status },
      responseType: 'text'
    }).subscribe({
      next: () => {
        this.facilities.set(this.facilities().filter(f => f.id !== facilityId));
        if (this.selectedFacility()?.id === facilityId) {
          this.selectedFacility.set(null);
          this.selectedCourts.set([]);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage.set(err.error?.message || err.message || 'Greška pri ažuriranju statusa.');
      }
    });
  }

  selectFacility(facility: Facility): void {
    this.selectedFacility.set(facility);
    this.selectedCourts.set([]);
    this.detailErrorMessage.set('');
    this.loadFacilityCourts(facility.id);
  }

  closeDetails(): void {
    this.selectedFacility.set(null);
    this.selectedCourts.set([]);
    this.detailErrorMessage.set('');
  }

  private loadFacilityCourts(facilityId: number): void {
    this.detailLoading.set(true);
    this.http.get<Court[]>(`http://localhost:8080/api/admin/facilities/${facilityId}/courts`).subscribe({
      next: courts => {
        this.selectedCourts.set(courts);
        this.detailLoading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.detailErrorMessage.set(err.error?.message || err.message || 'Greška pri učitavanju terena.');
        this.detailLoading.set(false);
      }
    });
  }
}
