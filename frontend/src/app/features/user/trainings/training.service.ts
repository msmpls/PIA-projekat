import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Coach, TrainingRequest, TrainingResponse } from '../../../core/models/training.model';

@Injectable({ providedIn: 'root' })
export class TrainingService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api';

  getCoaches(facilityId: number, sport: string): Observable<Coach[]> {
    return this.http.get<Coach[]>(`${this.base}/trainings/coaches`, {
      params: { facility_id: facilityId.toString(), sport }
    });
  }

  // NOVO: Svi trenere u objektu (za coach preview)
  getAllCoachesByFacility(facilityId: number): Observable<Coach[]> {
    return this.http.get<Coach[]>(`${this.base}/trainings/facility/${facilityId}/coaches`);
  }

  scheduleTraining(req: TrainingRequest): Observable<string> {
    return this.http.post(`${this.base}/trainings`, req, { responseType: 'text' });
  }

  getMyTrainings(): Observable<TrainingResponse[]> {
    return this.http.get<TrainingResponse[]>(`${this.base}/trainings/my`);
  }
}
