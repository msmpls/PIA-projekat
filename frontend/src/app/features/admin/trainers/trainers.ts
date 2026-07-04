import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrainersService } from './trainers.service';
import { Coach } from '../../../core/models/training.model';

@Component({
  selector: 'app-trainers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trainers.html',
  styleUrl: './trainers.css',
})
export class Trainers implements OnInit {
  private trainersService = inject(TrainersService);

  coaches = signal<Coach[]>([]);
  loading = signal(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  ngOnInit(): void {
    this.loadCoaches();
  }

  loadCoaches(): void {
    this.loading.set(true);
    this.trainersService.getAllCoaches().subscribe({
      next: (data) => {
        this.coaches.set(data || []);
        this.loading.set(false);
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri učitavanju trenera.');
        this.coaches.set([]);
        this.loading.set(false);
      },
    });
  }

  toggleActive(coach: Coach): void {
    const newState = !coach.is_active;
    this.trainersService.toggleActive(coach.id, newState).subscribe({
      next: () => {
        const label = newState ? 'aktiviran' : 'deaktiviran';
        this.showMessage('success', `Trener ${coach.name} je uspešno ${label}.`);
        this.loadCoaches();
      },
      error: (err) => {
        this.showMessage('error', err.error || 'Greška pri ažuriranju statusa trenera.');
      },
    });
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
