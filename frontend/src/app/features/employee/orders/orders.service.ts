import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderResponse } from '../../../core/models/shop.model';

@Injectable({ providedIn: 'root' })
export class EmployeeOrderService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/employee/dashboard/orders';

  getAllOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(this.base);
  }

  acceptOrder(id: number): Observable<string> {
    return this.http.put(`${this.base}/${id}/accept`, {}, { responseType: 'text' });
  }

  deliverOrder(id: number): Observable<string> {
    return this.http.put(`${this.base}/${id}/deliver`, {}, { responseType: 'text' });
  }

  cancelOrder(id: number): Observable<string> {
    return this.http.put(`${this.base}/${id}/cancel`, {}, { responseType: 'text' });
  }
}
