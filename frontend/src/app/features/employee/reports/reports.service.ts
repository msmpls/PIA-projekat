import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReportsService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/employee/reports';

  downloadCourtUsage(month: string): Observable<Blob> {
    return this.http.get(`${this.base}/court-usage?month=${month}`, { responseType: 'blob' });
  }

  downloadEquipmentSales(month: string): Observable<Blob> {
    return this.http.get(`${this.base}/equipment-sales?month=${month}`, { responseType: 'blob' });
  }
}
