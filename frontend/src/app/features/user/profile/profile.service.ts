import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminUserResponse } from '../../../core/models/user.model';
import { ReservationResponse } from '../../../core/models/reservation.model';
import { TrainingResponse } from '../../../core/models/training.model';
import { OrderResponse } from '../../../core/models/shop.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api';

  getProfile(): Observable<AdminUserResponse> {
    return this.http.get<AdminUserResponse>(`${this.base}/users/profile`);
  }

  updateProfile(data: { first_name: string; last_name: string; phone: string; email: string }): Observable<string> {
    return this.http.put(`${this.base}/users/profile`, data, { responseType: 'text' });
  }

  updateSports(sports: string): Observable<string> {
    return this.http.put(`${this.base}/users/profile/sports`, JSON.stringify(sports), {
      headers: { 'Content-Type': 'application/json' },
      responseType: 'text',
    });
  }

  updateImage(file: File): Observable<{ profile_picture_path: string }> {
    const fd = new FormData();
    fd.append('image', file);
    return this.http.put<{ profile_picture_path: string }>(`${this.base}/users/profile/image`, fd);
  }

  getMyReservations(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(`${this.base}/reservations/my`);
  }

  cancelReservation(id: number): Observable<string> {
    return this.http.put(`${this.base}/reservations/${id}/cancel`, {}, { responseType: 'text' });
  }

  getMyTrainings(): Observable<TrainingResponse[]> {
    return this.http.get<TrainingResponse[]>(`${this.base}/trainings/my`);
  }

  getMyOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.base}/shop/orders/my`);
  }

  cancelOrder(id: number): Observable<string> {
    return this.http.put(`${this.base}/shop/orders/${id}/cancel`, {}, { responseType: 'text' });
  }
}
