import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmployeeProfileResponse } from '../../../core/models/user.model';
import { FacilityDetails } from '../../../core/models/facility-details.model';
import { ReservationViewResponse } from '../../../core/models/reservation.model';

@Injectable({ providedIn: 'root' })
export class CalendarService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api';

  getEmployeeProfile(): Observable<EmployeeProfileResponse> {
    return this.http.get<EmployeeProfileResponse>(`${this.base}/users/profile/employee`);
  }

  getFacilityDetails(id: number): Observable<FacilityDetails> {
    return this.http.get<FacilityDetails>(`${this.base}/public/facilities/${id}`);
  }

  getCalendarData(courtId: number, startDate: string, endDate: string): Observable<ReservationViewResponse[]> {
    return this.http.get<ReservationViewResponse[]>(
      `${this.base}/employee/dashboard/calendar?court_id=${courtId}&startDate=${startDate}&endDate=${endDate}`
    );
  }

  moveReservation(reservationId: number, newDate: string, newStartTime: string, newEndTime: string): Observable<string> {
    return this.http.put(
      `${this.base}/employee/dashboard/move?reservationId=${reservationId}&newDate=${newDate}&newStartTime=${newStartTime}&newEndTime=${newEndTime}`,
      {}, { responseType: 'text' }
    );
  }
}
