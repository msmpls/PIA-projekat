import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Facility } from '../../../core/models/facility.model';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/public';

  search(name?: string, city?: string, type?: string, sport?: string): Observable<Facility[]> {
    let params = new HttpParams();
    if (name) {
      params = params.set('name', name);
    }
    if (city) {
      params = params.set('city', city);
    }
    if (type) {
      params = params.set('type', type);
    }
    if (sport) {
      params = params.set('sport', sport);
    }
    return this.http.get<Facility[]>(`${this.apiUrl}/facilities`, { params });
  }

  getCities(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/cities`);
  }

  getSports(): Observable<string[]> {
    return this.http.get<string[]>('http://localhost:8080/api/sports/names');
  }
}
