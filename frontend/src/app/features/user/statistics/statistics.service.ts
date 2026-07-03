import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StatisticsData {
  reservationsBySport: { sport: string; count: number }[];
  monthlyActivity: { month: string; count: number }[];
  totalSpendingOnEquipment: number;
}

@Injectable({ providedIn: 'root' })
export class StatisticsService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/statistics/my';

  getMyStatistics(): Observable<StatisticsData> {
    return this.http.get<StatisticsData>(this.url);
  }
}
