import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmployeeProfileResponse } from '../../../core/models/user.model';
import { CourtRequest } from '../../../core/models/facility.model';

@Injectable({ providedIn: 'root' })
export class FacilityService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api';

  getEmployeeProfile(): Observable<EmployeeProfileResponse> {
    return this.http.get<EmployeeProfileResponse>(`${this.base}/users/profile/employee`);
  }

  createFacilityFromJson(file: File): Observable<string> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post(`${this.base}/facilities/create`, fd, { responseType: 'text' });
  }

  addCourt(facilityId: number, court: CourtRequest): Observable<string> {
    return this.http.post(`${this.base}/facilities/${facilityId}/courts`, court, { responseType: 'text' });
  }
}
