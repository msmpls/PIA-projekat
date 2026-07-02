import { Component,inject, signal } from '@angular/core';
import { HomeService } from './home.service';
import { HomepageResponse } from '../../../core/models/homepage.model';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {

  private homeService = inject(HomeService);
  
  // Signal za čuvanje podataka
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
      }
    });
  }

}
