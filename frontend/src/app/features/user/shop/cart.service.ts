import { Injectable, signal, computed } from '@angular/core';
import { Equipment } from '../../../core/models/shop.model';

export interface CartItem {
  equipment: Equipment;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  items = signal<CartItem[]>([]);

  totalCount = computed(() => this.items().reduce((sum, i) => sum + i.quantity, 0));
  totalPrice = computed(() => this.items().reduce((sum, i) => sum + i.equipment.price * i.quantity, 0));

  add(equipment: Equipment): void {
    const existing = this.items().find(i => i.equipment.id === equipment.id);
    if (existing) {
      this.items.update(items => items.map(i =>
        i.equipment.id === equipment.id ? { ...i, quantity: i.quantity + 1 } : i
      ));
    } else {
      this.items.update(items => [...items, { equipment, quantity: 1 }]);
    }
  }

  updateQty(id: number, qty: number): void {
    if (qty <= 0) { this.remove(id); return; }
    this.items.update(items => items.map(i =>
      i.equipment.id === id ? { ...i, quantity: qty } : i
    ));
  }

  remove(id: number): void {
    this.items.update(items => items.filter(i => i.equipment.id !== id));
  }

  clear(): void {
    this.items.set([]);
  }
}
