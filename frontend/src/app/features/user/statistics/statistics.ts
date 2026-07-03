import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatisticsService, StatisticsData } from './statistics.service';

interface LinePoint {
  x: number;
  y: number;
  count: number;
  month: string;
  label: string;
}

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics.html',
  styleUrl: './statistics.css',
})
export class Statistics implements OnInit {
  private statsService = inject(StatisticsService);

  data = signal<StatisticsData | null>(null);
  loading = signal(true);

  private readonly monthNames = [
    'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
  ];

  maxSportCount = computed(() => {
    const d = this.data();
    if (!d || d.reservationsBySport.length === 0) return 1;
    return Math.max(...d.reservationsBySport.map((s) => s.count), 1);
  });

  maxMonthlyCount = computed(() => {
    const d = this.data();
    if (!d || d.monthlyActivity.length === 0) return 1;
    return Math.max(...d.monthlyActivity.map((m) => m.count), 1);
  });

  ngOnInit(): void {
    this.statsService.getMyStatistics().subscribe({
      next: (data) => {
        this.data.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  /** "2026-01" → "Jan 2026" */
  formatMonth(month: string): string {
    const parts = (month || '').split('-');
    if (parts.length !== 2) return month;
    const year = parts[0];
    const m = parseInt(parts[1], 10);
    if (isNaN(m) || m < 1 || m > 12) return month;
    return `${this.monthNames[m - 1]} ${year}`;
  }

  /** Build SVG points string "x1,y1 x2,y2 ..." for the line chart. */
  buildLinePoints(): string {
    return this.computeLinePoints()
      .map((p) => `${p.x},${p.y}`)
      .join(' ');
  }

  /** List of computed pixel points (for circles and labels). */
  linePointsList(): LinePoint[] {
    return this.computeLinePoints();
  }

  private computeLinePoints(): LinePoint[] {
    const d = this.data();
    if (!d || d.monthlyActivity.length === 0) return [];
    const W = 600;
    const H = 250;
    const padLeft = 40;
    const padRight = 20;
    const padTop = 20;
    const padBottom = 40;
    const plotW = W - padLeft - padRight;
    const plotH = H - padTop - padBottom;
    const baseline = padTop + plotH;
    const max = this.maxMonthlyCount();
    const n = d.monthlyActivity.length;
    return d.monthlyActivity.map((item, i) => {
      const x =
        n === 1
          ? padLeft + plotW / 2
          : padLeft + (i / (n - 1)) * plotW;
      const y = baseline - (item.count / max) * plotH;
      return {
        x: Math.round(x * 10) / 10,
        y: Math.round(y * 10) / 10,
        count: item.count,
        month: item.month,
        label: this.formatMonth(item.month),
      };
    });
  }
}
