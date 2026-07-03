import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservationViewResponse } from '../../../core/models/reservation.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/employee/dashboard';

  getCheckInTable(): Observable<ReservationViewResponse[]> {
    return this.http.get<ReservationViewResponse[]>(`${this.base}/table`);
  }

  getPending(): Observable<ReservationViewResponse[]> {
    return this.http.get<ReservationViewResponse[]>(`${this.base}/pending`);
  }

  checkIn(type: string, id: number, action: string): Observable<string> {
    return this.http.post(`${this.base}/check-in?type=${type}&id=${id}&action=${action}`, {}, { responseType: 'text' });
  }

  decidePending(id: number, approve: boolean): Observable<string> {
    return this.http.post(`${this.base}/pending/decide?id=${id}&approve=${approve}`, {}, { responseType: 'text' });
  }
}
