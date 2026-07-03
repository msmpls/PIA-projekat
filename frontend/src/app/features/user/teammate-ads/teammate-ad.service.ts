import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdRequest, AdResponse, AdResponseDetails } from '../../../core/models/teammate-ad.model';

@Injectable({ providedIn: 'root' })
export class TeammateAdService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/ads';

  getActiveAds(): Observable<AdResponse[]> {
    return this.http.get<AdResponse[]>(`${this.base}/active`);
  }

  createAd(req: AdRequest): Observable<string> {
    return this.http.post(`${this.base}`, req, { responseType: 'text' });
  }

  respondToAd(adId: number): Observable<string> {
    return this.http.post(`${this.base}/${adId}/respond`, {}, { responseType: 'text' });
  }

  getMyAds(): Observable<AdResponseDetails[]> {
    return this.http.get<AdResponseDetails[]>(`${this.base}/my`);
  }

  handleResponse(responseId: number, status: string): Observable<string> {
    return this.http.put(`${this.base}/responses/${responseId}/status?status=${status}`, {}, { responseType: 'text' });
  }

  closeAd(adId: number): Observable<string> {
    return this.http.put(`${this.base}/${adId}/close`, {}, { responseType: 'text' });
  }

  getMyResponses(): Observable<{ ad_id: number; status: string }[]> {
    return this.http.get<{ ad_id: number; status: string }[]>(`${this.base}/my-responses`);
  }
}
