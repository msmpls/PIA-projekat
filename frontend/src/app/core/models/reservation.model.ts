export interface ReservationRequest {
  facility_id: number;
  court_id: number;
  reservation_date: string; // "YYYY-MM-DD"
  start_time: string;       // "HH:MM:SS" npr. "18:00:00"
  end_time: string;         // "HH:MM:SS" npr. "19:00:00"
}

export interface ReservationResponse {
  id: number;
  user_id: number;
  facility_name: string;
  city: string;
  court_name_number: string;
  sport: string;
  reservation_date: string;
  start_time: string;
  end_time: string;
  status: string;
}

export interface Reservation {
  id: number;
  user_id: number;
  court_id: number;
  reservation_date: string;
  start_time: string;
  end_time: string;
  status: string; // PENDING | CONFIRMED | CANCELLED | ABSENT
}

// CAMELCASE - koristi se u employee-dashboard.service.ts
export interface ReservationViewResponse {
  id: number;
  type: 'RESERVATION' | 'TRAINING';
  userName: string;
  courtId: number;
  courtName: string;
  coachName: string | null;
  date: string;
  startTime: string;
  endTime: string;
  status: string; // SCHEDULED | CONFIRMED | MISSED
  actionAllowed: boolean;
}