import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SportsAdminService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api';

  getSportNames(): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/sports/names`);
  }

  addSport(name: string, description: string): Observable<string> {
    return this.http.post(`${this.base}/admin/sports`, { name, description }, { responseType: 'text' });
  }
}
