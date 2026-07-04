import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SportsAdminService } from './sports.service';

@Component({
  selector: 'app-admin-sports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './sports.html',
  styleUrl: './sports.css',
})
export class Sports implements OnInit {
  private fb = inject(FormBuilder);
  private sportsService = inject(SportsAdminService);

  sports = signal<string[]>([]);
  loading = signal(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  addForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadSports();
  }

  loadSports(): void {
    this.loading.set(true);
    this.sportsService.getSportNames().subscribe({
      next: (names) => {
        this.sports.set(names);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.showMessage('error', 'Greška pri učitavanju sportova.');
      },
    });
  }

  addSport(): void {
    if (this.addForm.invalid) {
      this.addForm.markAllAsTouched();
      return;
    }
    const { name, description } = this.addForm.getRawValue();
    this.sportsService.addSport(name, description).subscribe({
      next: () => {
        this.addForm.reset();
        this.loadSports();
        this.showMessage('success', 'Sport je uspešno dodat.');
      },
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : (err.error?.message || 'Greška pri dodavanju sporta.');
        this.showMessage('error', msg);
      },
    });
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
