import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { SearchService } from './search.service';
import { Facility } from '../../../core/models/facility.model';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgFor],
  templateUrl: './search.html',
  styleUrl: './search.css',
})
export class Search {
  private fb = inject(FormBuilder);
  private searchService = inject(SearchService);

  form = this.fb.nonNullable.group({
    name: [''],
    city: [''],
    type: ['']
  });

  results = signal<Facility[]>([]);
  loading = signal(false);
  error = signal<string>('');

  search() {
    this.error.set('');
    this.loading.set(true);
    this.results.set([]);

    const { name, city, type } = this.form.value;
    this.searchService.search(name, city, type).subscribe({
      next: (facilities) => {
        this.results.set(facilities);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Greška pri pretrazi objekata.');
        this.loading.set(false);
      }
    });
  }
}
