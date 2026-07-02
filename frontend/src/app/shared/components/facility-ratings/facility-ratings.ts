import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RatingService } from './facility-ratings.service';
import { AuthService } from '../../../core/auth/auth.service';
import { RatingSummary, RatingEligibility } from '../../../core/models/rating.model';

@Component({
  selector: 'app-facility-ratings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './facility-ratings.html',
  styleUrl: './facility-ratings.css',
})
export class FacilityRatings implements OnInit {
  @Input({ required: true }) facilityId!: number;

  private ratingService = inject(RatingService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  summary = signal<RatingSummary | null>(null);
  eligibility = signal<RatingEligibility | null>(null);
  loading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  currentUsername = this.authService.currentUser()?.username ?? null;
  isAthlete = this.authService.getRole() === 'ATHLETE';

  form = this.fb.nonNullable.group({
    is_like: [true, Validators.required],
    comment: ['', Validators.maxLength(500)],
  });

  ngOnInit(): void {
    this.loadSummary();
    if (this.isAthlete) {
      this.loadEligibility();
    }
  }

  loadSummary(): void {
    this.loading.set(true);
    this.ratingService.getSummary(this.facilityId).subscribe({
      next: summary => {
        this.summary.set(summary);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  loadEligibility(): void {
    this.ratingService.getEligibility(this.facilityId).subscribe({
      next: eligibility => this.eligibility.set(eligibility),
      error: () => this.eligibility.set(null),
    });
  }

  submit(): void {
    this.errorMessage.set('');
    this.successMessage.set('');

    if (this.form.invalid) {
      return;
    }

    const raw = this.form.getRawValue();
    this.ratingService.addRating(this.facilityId, {
      is_like: raw.is_like,
      comment: raw.comment,
    }).subscribe({
      next: () => {
        this.successMessage.set('Hvala na oceni!');
        this.form.reset({ is_like: true, comment: '' });
        this.loadSummary();
        this.loadEligibility();
      },
      error: err => {
        this.errorMessage.set(err.error || err.error?.message || 'Greška pri slanju ocene.');
      }
    });
  }
}