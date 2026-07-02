export interface Coach {
  id: number;
  facility_id: number;
  name: string;
  specialization: string;
  sport: string;
  price_per_hour: number;
  average_rating: number;
  is_active: boolean;
}

export interface TrainingRequest {
  coach_id: number;
  court_id: number;
  training_date: string;
  start_time: string;
  end_time: string;
}

export interface TrainingResponse {
  id: number;
  coach_name: string;
  facility_name: string;
  court_id: number;
  court_name: string;
  sport: string;
  training_date: string;
  start_time: string;
  end_time: string;
  status: string; // SCHEDULED | COMPLETED | CANCELLED
}

export interface Training {
  id: number;
  user_id: number;
  coach_id: number;
  court_id: number;
  training_date: string;
  start_time: string;
  end_time: string;
  status: string;
}