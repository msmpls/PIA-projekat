import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ShopService } from './shop.service';
import { CartService } from './cart.service';
import { Equipment } from '../../../core/models/shop.model';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './shop.html',
  styleUrl: './shop.css',
})
export class Shop implements OnInit {
  private shopService = inject(ShopService);
  private http = inject(HttpClient);
  cart = inject(CartService);

  private base = 'http://localhost:8080';
  private sportsUrl = `${this.base}/api/sports/names`;

  sports = signal<string[]>([]);
  selectedSport = signal<string>('');
  catalog = signal<Equipment[]>([]);
  showCart = signal(false);
  loading = signal(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  ngOnInit(): void {
    this.http.get<string[]>(this.sportsUrl).subscribe({
      next: (sports) => {
        this.sports.set(sports);
        if (sports.length > 0) {
          this.selectSport(sports[0]);
        }
      },
      error: () => this.showMessage('error', 'Greška pri učitavanju sportova.'),
    });
  }

  selectSport(sport: string): void {
    this.selectedSport.set(sport);
    this.loading.set(true);
    this.shopService.getCatalog(sport).subscribe({
      next: (items) => {
        this.catalog.set(items);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.showMessage('error', 'Greška pri učitavanju kataloga.');
      },
    });
  }

  addToCart(equipment: Equipment): void {
    this.cart.add(equipment);
    this.showMessage('success', `${equipment.name} dodat u korpu.`);
  }

  updateQty(id: number, qty: number): void {
    this.cart.updateQty(id, qty);
  }

  removeItem(id: number): void {
    this.cart.remove(id);
  }

  toggleCart(): void {
    this.showCart.update((v) => !v);
  }

  checkout(): void {
    const items = this.cart.items().map((i) => ({
      equipment_id: i.equipment.id,
      quantity: i.quantity,
    }));
    if (items.length === 0) return;
    this.shopService.placeOrder(items).subscribe({
      next: () => {
        this.showMessage('success', 'Porudžbina je uspešno kreirana!');
        this.cart.clear();
        this.showCart.set(false);
      },
      error: () => this.showMessage('error', 'Greška pri poručivanju.'),
    });
  }

  getImageUrl(equipment: Equipment): string | null {
    const p = equipment.image_path;
    if (!p) return null;
    if (p.startsWith('/uploads')) return this.base + p;
    return p;
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
