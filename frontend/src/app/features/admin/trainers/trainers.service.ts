import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Coach } from '../../../core/models/training.model';

@Injectable({ providedIn: 'root' })
export class TrainersService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/admin/coaches';

  getAllCoaches(): Observable<Coach[]> {
    return this.http.get<Coach[]>(this.base);
  }

  toggleActive(id: number, isActive: boolean): Observable<string> {
    return this.http.put(`${this.base}/${id}/active?isActive=${isActive}`, {}, { responseType: 'text' });
  }
}
