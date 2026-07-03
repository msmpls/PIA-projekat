import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Equipment, OrderResponse } from '../../../core/models/shop.model';

@Injectable({ providedIn: 'root' })
export class ShopService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/shop';

  getCatalog(sportType: string): Observable<Equipment[]> {
    return this.http.get<Equipment[]>(`${this.base}/catalog`, { params: { sport_type: sportType } });
  }

  placeOrder(items: { equipment_id: number; quantity: number }[]): Observable<string> {
    return this.http.post(`${this.base}/orders`, { items }, { responseType: 'text' });
  }

  getMyOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.base}/orders/my`);
  }
}
