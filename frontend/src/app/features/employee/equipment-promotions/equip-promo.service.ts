import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Equipment } from '../../../core/models/shop.model';
import { Promotion } from '../../../core/models/promotion.model';

@Injectable({ providedIn: 'root' })
export class EquipPromoService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api';

  // Equipment
  getCatalog(sportType: string): Observable<Equipment[]> {
    return this.http.get<Equipment[]>(`${this.base}/shop/catalog`, { params: { sport_type: sportType } });
  }
  createEquipment(fd: FormData): Observable<string> {
    return this.http.post(`${this.base}/employee/equipment`, fd, { responseType: 'text' });
  }
  updatePrice(id: number, price: number): Observable<string> {
    return this.http.put(`${this.base}/employee/equipment/${id}/price`, price, { responseType: 'text' });
  }
  updateStock(id: number, stock: number): Observable<string> {
    return this.http.put(`${this.base}/employee/equipment/${id}/stock`, stock, { responseType: 'text' });
  }
  deleteEquipment(id: number): Observable<string> {
    return this.http.delete(`${this.base}/employee/equipment/${id}`, { responseType: 'text' });
  }

  // Promotions
  getPromotions(): Observable<Promotion[]> {
    return this.http.get<Promotion[]>(`${this.base}/employee/promotions`);
  }
  createPromotion(p: Promotion): Observable<string> {
    return this.http.post(`${this.base}/employee/promotions`, p, { responseType: 'text' });
  }
  updatePromotion(id: number, p: Promotion): Observable<string> {
    return this.http.put(`${this.base}/employee/promotions/${id}`, p, { responseType: 'text' });
  }
  deletePromotion(id: number): Observable<string> {
    return this.http.delete(`${this.base}/employee/promotions/${id}`, { responseType: 'text' });
  }
}
