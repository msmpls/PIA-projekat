import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ReportsService } from './reports.service';

@Component({
  selector: 'app-employee-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class Reports {
  private fb = inject(FormBuilder);
  private reportsService = inject(ReportsService);

  loading1 = signal(false);
  loading2 = signal(false);
  message = signal<{ type: 'success' | 'error'; text: string } | null>(null);

  form = this.fb.nonNullable.group({
    month: [this.currentMonth()],
  });

  private currentMonth(): string {
    const now = new Date();
    const y = now.getFullYear();
    const m = (now.getMonth() + 1).toString().padStart(2, '0');
    return `${y}-${m}`;
  }

  getMonthValue(): string {
    return this.form.controls.month.value || '';
  }

  downloadCourtUsage(): void {
    const month = this.getMonthValue();
    if (!month) {
      this.showMessage('error', 'Izaberite mesec.');
      return;
    }
    this.loading1.set(true);
    this.reportsService.downloadCourtUsage(month).subscribe({
      next: (data) => {
        this.triggerDownload(data, `court-usage-${month}.pdf`);
        this.loading1.set(false);
        this.showMessage('success', 'Izveštaj o popunjenosti terena je preuzet.');
      },
      error: () => {
        this.loading1.set(false);
        this.showMessage('error', 'Greška pri preuzimanju izveštaja.');
      },
    });
  }

  downloadEquipmentSales(): void {
    const month = this.getMonthValue();
    if (!month) {
      this.showMessage('error', 'Izaberite mesec.');
      return;
    }
    this.loading2.set(true);
    this.reportsService.downloadEquipmentSales(month).subscribe({
      next: (data) => {
        this.triggerDownload(data, `equipment-sales-${month}.pdf`);
        this.loading2.set(false);
        this.showMessage('success', 'Izveštaj o prometu opreme je preuzet.');
      },
      error: () => {
        this.loading2.set(false);
        this.showMessage('error', 'Greška pri preuzimanju izveštaja.');
      },
    });
  }

  private triggerDownload(data: Blob, filename: string): void {
    const blob = new Blob([data], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  private showMessage(type: 'success' | 'error', text: string): void {
    this.message.set({ type, text });
    setTimeout(() => this.message.set(null), 4000);
  }
}
