import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeeOrderService } from './orders.service';
import { OrderResponse } from '../../../core/models/shop.model';

@Component({
  selector: 'app-employee-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.html',
  styleUrl: './orders.css',
})
export class EmployeeOrders implements OnInit {
  private orderService = inject(EmployeeOrderService);

  orders = signal<OrderResponse[]>([]);
  loading = signal(true);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true);
    this.orderService.getAllOrders().subscribe({
      next: (data) => {
        this.orders.set(data || []);
        this.loading.set(false);
      },
      error: () => {
        this.showMessage('error', 'Greška pri učitavanju narudžbina.');
        this.loading.set(false);
      },
    });
  }

  acceptOrder(id: number): void {
    this.orderService.acceptOrder(id).subscribe({
      next: () => {
        this.showMessage('success', 'Narudžbina je prihvaćena.');
        this.loadOrders();
      },
      error: () => this.showMessage('error', 'Greška pri prihvatanju narudžbine.'),
    });
  }

  deliverOrder(id: number): void {
    this.orderService.deliverOrder(id).subscribe({
      next: () => {
        this.showMessage('success', 'Narudžbina je označena kao preuzeta.');
        this.loadOrders();
      },
      error: () => this.showMessage('error', 'Greška pri ažuriranju narudžbine.'),
    });
  }

  cancelOrder(id: number): void {
    if (!confirm('Da li ste sigurni da želite da otkažete ovu narudžbinu? Zalihe će biti vraćene.')) return;
    this.orderService.cancelOrder(id).subscribe({
      next: () => {
        this.showMessage('success', 'Narudžbina je otkazana.');
        this.loadOrders();
      },
      error: () => this.showMessage('error', 'Greška pri otkazivanju narudžbine.'),
    });
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'ORDERED': return 'Naručeno';
      case 'ACCEPTED': return 'Prihvaćeno';
      case 'DELIVERED': return 'Preuzeto';
      case 'CANCELLED': return 'Otkazano';
      default: return status;
    }
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
