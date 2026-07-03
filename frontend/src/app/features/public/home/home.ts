import { Component, inject, signal } from '@angular/core';
import { NgFor, NgIf, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { HomeService } from './home.service';
import { HomepageResponse } from '../../../core/models/homepage.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private homeService = inject(HomeService);
  private router = inject(Router);

  homeData = signal<HomepageResponse | null>(null);
  loading = signal(true);

  ngOnInit() {
    this.homeService.getHomepageData().subscribe({
      next: (data) => {
        this.homeData.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Greška pri učitavanju:', err);
        this.loading.set(false);
      },
    });
  }

  goToFacility(id: number): void {
    this.router.navigate(['/facility', id]);
  }
}
