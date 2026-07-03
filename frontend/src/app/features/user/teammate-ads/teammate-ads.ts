import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TeammateAdService } from './teammate-ad.service';
import { AdResponse, AdResponseDetails } from '../../../core/models/teammate-ad.model';

@Component({
  selector: 'app-teammate-ads',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './teammate-ads.html',
  styleUrl: './teammate-ads.css',
})
export class TeammateAds implements OnInit {
  private adService = inject(TeammateAdService);
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);

  activeTab = signal<'browse' | 'create' | 'my'>('browse');
  activeAds = signal<AdResponse[]>([]);
  myAds = signal<AdResponseDetails[]>([]);
  sports = signal<string[]>([]);
  cities = signal<string[]>([]);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  // Mapa adId -> status prijave (PENDING/APPROVED/REJECTED) za trenutnog korisnika
  myResponses = signal<Map<number, string>>(new Map());

  today = new Date().toISOString().split('T')[0];

  createForm = this.fb.nonNullable.group({
    sport: ['', Validators.required],
    city: ['', Validators.required],
    event_date: ['', Validators.required],
    event_time: ['', Validators.required],
    players_needed: [1, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.loadActiveAds();
    this.loadMyResponses();
    this.loadSports();
    this.loadCities();
  }

  loadSports(): void {
    this.http.get<string[]>('http://localhost:8080/api/sports/names').subscribe({
      next: (data) => this.sports.set(data || []),
      error: () => this.sports.set([]),
    });
  }

  loadCities(): void {
    this.http.get<string[]>('http://localhost:8080/api/public/cities').subscribe({
      next: (data) => this.cities.set(data || []),
      error: () => this.cities.set([]),
    });
  }

  loadActiveAds(): void {
    this.adService.getActiveAds().subscribe({
      next: (data) => this.activeAds.set(data || []),
      error: () => this.activeAds.set([]),
    });
  }

  loadMyResponses(): void {
    this.adService.getMyResponses().subscribe({
      next: (data) => {
        const map = new Map<number, string>();
        for (const r of data || []) {
          map.set(r.ad_id, r.status);
        }
        this.myResponses.set(map);
      },
      error: () => this.myResponses.set(new Map()),
    });
  }

  loadMyAds(): void {
    this.adService.getMyAds().subscribe({
      next: (data) => this.myAds.set(data || []),
      error: () => this.myAds.set([]),
    });
  }

  createAd(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      this.showMessage('error', 'Popunite sva obavezna polja.');
      return;
    }
    const value = this.createForm.getRawValue();
    const req = {
      sport: value.sport,
      city: value.city,
      event_date: value.event_date,
      event_time: value.event_time,
      players_needed: Number(value.players_needed),
    };
    this.adService.createAd(req).subscribe({
      next: () => {
        this.createForm.reset({ players_needed: 1 });
        this.showMessage('success', 'Oglas je uspešno postavljen.');
        this.setTab('browse');
        this.loadActiveAds();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri postavljanju oglasa.');
      },
    });
  }

  respondToAd(adId: number): void {
    this.adService.respondToAd(adId).subscribe({
      next: () => {
        // Azuriramo mapu sa PENDING statusom
        const map = new Map(this.myResponses());
        map.set(adId, 'PENDING');
        this.myResponses.set(map);
        this.showMessage('success', 'Prijavili ste se na oglas. Čeka odobrenje autora.');
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri prijavi na oglas.');
      },
    });
  }

  approveResponse(responseId: number): void {
    this.adService.handleResponse(responseId, 'APPROVED').subscribe({
      next: () => {
        this.showMessage('success', 'Zahtev je odobren.');
        this.loadMyAds();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri odobrenju zahteva.');
      },
    });
  }

  rejectResponse(responseId: number): void {
    this.adService.handleResponse(responseId, 'REJECTED').subscribe({
      next: () => {
        this.showMessage('success', 'Zahtev je odbijen.');
        this.loadMyAds();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri odbijanju zahteva.');
      },
    });
  }

  closeAd(adId: number): void {
    if (!confirm('Da li ste sigurni da želite da zatvorite ovaj oglas?')) return;
    this.adService.closeAd(adId).subscribe({
      next: () => {
        this.showMessage('success', 'Oglas je zatvoren.');
        this.loadMyAds();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri zatvaranju oglasa.');
      },
    });
  }

  setTab(tab: 'browse' | 'create' | 'my'): void {
    this.activeTab.set(tab);
    if (tab === 'browse') this.loadActiveAds();
    if (tab === 'my') this.loadMyAds();
  }

  getResponseStatus(adId: number): string | null {
    return this.myResponses().get(adId) ?? null;
  }

  hasResponded(adId: number): boolean {
    return this.myResponses().has(adId);
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
