import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SearchService } from './search.service';
import { Facility } from '../../../core/models/facility.model';

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

  form = this.fb.nonNullable.group({
    name: [''],
    city: [''],
    type: [''],
    sport: [''],
  });

  results = signal<Facility[]>([]);
  loading = signal(false);
  error = signal<string>('');
  cities = signal<string[]>([]);
  sports = signal<string[]>([]);
  hasSearched = signal(false);

  // Sorting
  sortKey = signal<SortKey>('name');
  sortDir = signal<SortDir>('asc');

  // Computed sorted results
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
        this.results.set(facilities);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Greška pri pretrazi objekata.');
        this.loading.set(false);
      },
    });
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
