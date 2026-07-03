export interface Promotion {
  id: number;
  name: string;
  discount_percent: number;
  sport_type: string;
  start_date: string; // LocalDate -> "YYYY-MM-DD"
  end_date: string;
  is_active: boolean;
}

// Kratak prikaz promocije za pocetnu stranicu (camelCase iz bekenda)
export interface PromotionSummary {
  promotionName: string;
  sportType: string;
  startDate: string;
  endDate: string;
  discountPercent: number;
}