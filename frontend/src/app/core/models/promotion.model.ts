export interface Promotion {
  id: number;
  name: string;
  discount_percent: number;
  sport_type: string;
  start_date: string; // LocalDate -> "YYYY-MM-DD"
  end_date: string;
  is_active: boolean;
}