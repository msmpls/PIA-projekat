import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SearchService } from './search.service';
import { Facility } from '../../../core/models/facility.model';
import { AuthService } from '../../../core/auth/auth.service';
import { HttpClient } from '@angular/common/http';

type SortKey = 'name' | 'city' | 'sports' | 'price_per_hour';
type SortDir = 'asc' | 'desc';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './search.html',
  styleUrl: './search.css',
})
export class Search implements OnInit {
  private fb = inject(FormBuilder);
  private searchService = inject(SearchService);
  private router = inject(Router);
  private authService = inject(AuthService);
  private http = inject(HttpClient);

  form = this.fb.nonNullable.group({
    name: [''],
    city: [''],
    type: [''],
    sport: [''],
    onlyFreeToday: [false],
  });

  results = signal<Facility[]>([]);
  loading = signal(false);
  error = signal<string>('');
  cities = signal<string[]>([]);
  sports = signal<string[]>([]);
  hasSearched = signal(false);

  // Athlete-only: "samo slobodni danas" checkbox
  isAthlete = this.authService.getRole() === 'ATHLETE';
  filteringFree = signal(false);

  // Sorting
  sortKey = signal<SortKey>('name');
  sortDir = signal<SortDir>('asc');

  sortedResults = computed(() => {
    const data = [...this.results()];
    const key = this.sortKey();
    const dir = this.sortDir() === 'asc' ? 1 : -1;
    return data.sort((a, b) => {
      let valA: string | number;
      let valB: string | number;
      if (key === 'price_per_hour') {
        valA = a.price_per_hour ?? 0;
        valB = b.price_per_hour ?? 0;
      } else if (key === 'sports') {
        valA = (a.sports ?? '').toLowerCase();
        valB = (b.sports ?? '').toLowerCase();
      } else {
        valA = (a[key] ?? '').toString().toLowerCase();
        valB = (b[key] ?? '').toString().toLowerCase();
      }
      if (valA < valB) return -1 * dir;
      if (valA > valB) return 1 * dir;
      return 0;
    });
  });

  ngOnInit(): void {
    this.loadCities();
    this.loadSports();
  }

  loadCities(): void {
    this.searchService.getCities().subscribe({
      next: (data) => this.cities.set(data),
      error: () => this.cities.set([]),
    });
  }

  loadSports(): void {
    this.searchService.getSports().subscribe({
      next: (data) => this.sports.set(data),
      error: () => this.sports.set([]),
    });
  }

  search(): void {
    this.error.set('');
    this.loading.set(true);
    this.results.set([]);
    this.hasSearched.set(true);

    const { name, city, type, sport } = this.form.value;
    this.searchService.search(name, city, type, sport).subscribe({
      next: (facilities) => {
        // Ako je "samo slobodni danas" ukljuceno i korisnik je sportista, filtriramo
        if (this.isAthlete && this.form.value.onlyFreeToday) {
          this.filterFreeToday(facilities);
        } else {
          this.results.set(facilities);
          this.loading.set(false);
        }
      },
      error: () => {
        this.error.set('Greška pri pretrazi objekata.');
        this.loading.set(false);
      },
    });
  }

  // Filtrira objekte koji imaju bar jedan slobodan termin danas
  private filterFreeToday(facilities: Facility[]): void {
    this.filteringFree.set(true);
    const today = new Date().toISOString().split('T')[0];
    const freeFacilities: Facility[] = [];
    let checked = 0;
    const total = facilities.length;

    if (total === 0) {
      this.results.set([]);
      this.loading.set(false);
      this.filteringFree.set(false);
      return;
    }

    for (const facility of facilities) {
      // Dohvati detalje objekta da dobijemo listu terena
      this.http.get<any>(`http://localhost:8080/api/public/facilities/${facility.id}`).subscribe({
        next: (details) => {
          const courts = details.courts || [];
          if (courts.length === 0) {
            // Nema terena - preskoci
            checked++;
            if (checked === total) {
              this.results.set(freeFacilities);
              this.loading.set(false);
              this.filteringFree.set(false);
            }
            return;
          }

          // Proveri zauzece za svaki teren danas
          let courtsChecked = 0;
          let hasFree = false;

          for (const court of courts) {
            this.http.get<any[]>(`http://localhost:8080/api/reservations/court/${court.id}?date=${today}`).subscribe({
              next: (reservations) => {
                // Odredi radno vreme
                const workStart = parseInt(details.facility.work_start_time?.split(':')[0] || '8');
                const workEnd = parseInt(details.facility.work_end_time?.split(':')[0] || '22');
                const occupiedHours = new Set<number>();
                for (const r of reservations) {
                  const sh = parseInt(r.start_time.split(':')[0]);
                  const eh = parseInt(r.end_time.split(':')[0]);
                  for (let h = sh; h < eh; h++) occupiedHours.add(h);
                }
                // Da li ima bar jedan slobodan sat?
                for (let h = workStart; h < workEnd; h++) {
                  if (!occupiedHours.has(h) && h >= new Date().getHours()) {
                    hasFree = true;
                    break;
                  }
                }

                courtsChecked++;
                if (courtsChecked === courts.length) {
                  if (hasFree) freeFacilities.push(facility);
                  checked++;
                  if (checked === total) {
                    this.results.set(freeFacilities);
                    this.loading.set(false);
                    this.filteringFree.set(false);
                  }
                }
              },
              error: () => {
                courtsChecked++;
                if (courtsChecked === courts.length) {
                  checked++;
                  if (checked === total) {
                    this.results.set(freeFacilities);
                    this.loading.set(false);
                    this.filteringFree.set(false);
                  }
                }
              },
            });
          }
        },
        error: () => {
          checked++;
          if (checked === total) {
            this.results.set(freeFacilities);
            this.loading.set(false);
            this.filteringFree.set(false);
          }
        },
      });
    }
  }

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

  goToDetails(facilityId: number): void {
    this.router.navigate(['/facility', facilityId]);
  }
}
