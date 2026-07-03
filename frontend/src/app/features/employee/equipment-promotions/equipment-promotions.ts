import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EquipPromoService } from './equip-promo.service';
import { Equipment } from '../../../core/models/shop.model';
import { Promotion } from '../../../core/models/promotion.model';

@Component({
  selector: 'app-equipment-promotions',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './equipment-promotions.html',
  styleUrl: './equipment-promotions.css',
})
export class EquipmentPromotions implements OnInit {
  private svc = inject(EquipPromoService);
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);

  private base = 'http://localhost:8080';

  activeTab = signal<'equipment' | 'promotions'>('equipment');
  sports = signal<string[]>([]);
  selectedSport = signal<string>('');
  equipmentList = signal<Equipment[]>([]);
  promotions = signal<Promotion[]>([]);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);
  loading = signal(false);

  selectedFile: File | null = null;

  equipmentForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    sportType: ['', Validators.required],
    description: [''],
  });

  promotionForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    discount_percent: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    sport_type: ['', Validators.required],
    start_date: ['', Validators.required],
    end_date: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadSports();
    this.loadPromotions();
  }

  loadSports(): void {
    this.http.get<string[]>('http://localhost:8080/api/sports/names').subscribe({
      next: (data) => {
        this.sports.set(data || []);
        if ((data || []).length > 0) {
          this.selectSport(data[0]);
        }
      },
      error: () => this.sports.set([]),
    });
  }

  loadPromotions(): void {
    this.svc.getPromotions().subscribe({
      next: (data) => this.promotions.set(data || []),
      error: () => this.promotions.set([]),
    });
  }

  selectSport(sport: string): void {
    this.selectedSport.set(sport);
    this.loading.set(true);
    this.svc.getCatalog(sport).subscribe({
      next: (items) => {
        this.equipmentList.set(items || []);
        this.loading.set(false);
      },
      error: () => {
        this.equipmentList.set([]);
        this.loading.set(false);
        this.showMessage('error', 'Greška pri učitavanju kataloga.');
      },
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files && input.files.length > 0 ? input.files[0] : null;
  }

  addEquipment(): void {
    if (this.equipmentForm.invalid) {
      this.equipmentForm.markAllAsTouched();
      this.showMessage('error', 'Popunite sva obavezna polja.');
      return;
    }
    const v = this.equipmentForm.getRawValue();
    const fd = new FormData();
    fd.append('name', v.name);
    fd.append('price', String(v.price));
    fd.append('stock', String(v.stock));
    fd.append('sportType', v.sportType);
    fd.append('description', v.description);
    if (this.selectedFile) {
      fd.append('file', this.selectedFile);
    }
    this.svc.createEquipment(fd).subscribe({
      next: () => {
        this.showMessage('success', 'Oprema je uspešno dodata.');
        this.equipmentForm.reset({ price: 0, stock: 0, description: '' });
        this.selectedFile = null;
        this.selectSport(this.selectedSport());
      },
      error: (err) => this.showMessage('error', err.error || 'Greška pri dodavanju opreme.'),
    });
  }

  updatePrice(id: number): void {
    const input = prompt('Unesite novu cenu:');
    if (input === null) return;
    const price = Number(input);
    if (isNaN(price) || price < 0) {
      this.showMessage('error', 'Nevažeća cena.');
      return;
    }
    this.svc.updatePrice(id, price).subscribe({
      next: () => {
        this.showMessage('success', 'Cena je ažurirana.');
        this.selectSport(this.selectedSport());
      },
      error: (err) => this.showMessage('error', err.error || 'Greška pri ažuriranju cene.'),
    });
  }

  updateStock(id: number): void {
    const input = prompt('Unesite novu količinu:');
    if (input === null) return;
    const stock = Number(input);
    if (!Number.isInteger(stock) || stock < 0) {
      this.showMessage('error', 'Nevažeća količina.');
      return;
    }
    this.svc.updateStock(id, stock).subscribe({
      next: () => {
        this.showMessage('success', 'Zaliha je ažurirana.');
        this.selectSport(this.selectedSport());
      },
      error: (err) => this.showMessage('error', err.error || 'Greška pri ažuriranju zalihe.'),
    });
  }

  deleteEquipment(id: number): void {
    if (!confirm('Da li ste sigurni da želite da obrišete ovu opremu?')) return;
    this.svc.deleteEquipment(id).subscribe({
      next: () => {
        this.showMessage('success', 'Oprema je obrisana.');
        this.selectSport(this.selectedSport());
      },
      error: (err) => this.showMessage('error', err.error || 'Greška pri brisanju opreme.'),
    });
  }

  addPromotion(): void {
    if (this.promotionForm.invalid) {
      this.promotionForm.markAllAsTouched();
      this.showMessage('error', 'Popunite sva obavezna polja.');
      return;
    }
    const v = this.promotionForm.getRawValue();
    const promo: Promotion = {
      id: 0,
      name: v.name,
      discount_percent: Number(v.discount_percent),
      sport_type: v.sport_type,
      start_date: v.start_date,
      end_date: v.end_date,
      is_active: true,
    };
    this.svc.createPromotion(promo).subscribe({
      next: () => {
        this.showMessage('success', 'Promocija je uspešno dodata.');
        this.promotionForm.reset({ discount_percent: 0 });
        this.loadPromotions();
      },
      error: (err) => this.showMessage('error', err.error || 'Greška pri dodavanju promocije.'),
    });
  }

  toggleActive(p: Promotion): void {
    const updated: Promotion = { ...p, is_active: !p.is_active };
    this.svc.updatePromotion(p.id, updated).subscribe({
      next: () => {
        this.showMessage('success', 'Status promocije je ažuriran.');
        this.loadPromotions();
      },
      error: (err) => this.showMessage('error', err.error || 'Greška pri ažuriranju promocije.'),
    });
  }

  deletePromotion(id: number): void {
    if (!confirm('Da li ste sigurni da želite da obrišete ovu promociju?')) return;
    this.svc.deletePromotion(id).subscribe({
      next: () => {
        this.showMessage('success', 'Promocija je obrisana.');
        this.loadPromotions();
      },
      error: (err) => this.showMessage('error', err.error || 'Greška pri brisanju promocije.'),
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
