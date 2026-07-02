export interface AdRequest {
  sport: string;
  city: string;
  event_date: string;
  event_time: string;
  players_needed: number;
}

export interface AdResponse {
  id: number;
  creator_username: string;
  sport: string;
  city: string;
  event_date: string;
  event_time: string;
  players_needed: number;
  status: string; // OPEN | CLOSED
}

export interface TeammateResponseDTO {
  response_id: number;
  user_id: number;
  username: string;
  first_name: string;
  last_name: string;
  phone: string;
  status: string; // PENDING | APPROVED | REJECTED
}

export interface AdResponseDetails {
  id: number;
  sport: string;
  city: string;
  event_date: string;
  event_time: string;
  players_needed: number;
  status: string;
  responses: TeammateResponseDTO[];
}

export interface TeammateAd {
  id: number;
  creator_id: number;
  sport: string;
  city: string;
  event_date: string;
  event_time: string;
  players_needed: number;
  status: string;
}