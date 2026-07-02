import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HomepageResponse } from '../../../core/models/homepage.model';

@Injectable({ providedIn: 'root' })
export class HomeService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/public/homepage';

  getHomepageData(): Observable<HomepageResponse> {
    return this.http.get<HomepageResponse>(this.apiUrl);
  }
}