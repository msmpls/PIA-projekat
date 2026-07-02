import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Facility } from '../../../core/models/facility.model';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/public/facilities';

  search(name?: string, city?: string, type?: string): Observable<Facility[]> {
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
    return this.http.get<Facility[]>(this.apiUrl, { params });
  }
}
