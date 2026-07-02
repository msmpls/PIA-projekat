import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RatingSummary, RatingEligibility, RatingRequest } from '../../../core/models/rating.model';

@Injectable({ providedIn: 'root' })
export class RatingService {
  private http = inject(HttpClient);
  private publicUrl = 'http://localhost:8080/api/public/facilities';
  private authUrl = 'http://localhost:8080/api/facilities';

  getSummary(facilityId: number): Observable<RatingSummary> {
    return this.http.get<RatingSummary>(`${this.publicUrl}/${facilityId}/ratings`);
  }

  getEligibility(facilityId: number): Observable<RatingEligibility> {
    return this.http.get<RatingEligibility>(`${this.authUrl}/${facilityId}/ratings/eligibility`);
  }

  addRating(facilityId: number, request: RatingRequest): Observable<string> {
    return this.http.post(`${this.authUrl}/${facilityId}/ratings`, request, { responseType: 'text' });
  }
}