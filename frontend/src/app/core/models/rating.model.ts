export interface RatingResponse {
  id: number;
  username: string;
  is_like: boolean;
  comment: string;
  created_at: string;
}

export interface RatingSummary {
  likes: number;
  dislikes: number;
  lastComments: RatingResponse[];
}

export interface RatingEligibility {
  confirmedReservations: number;
  usedRatings: number;
  canRate: boolean;
}

export interface RatingRequest {
  is_like: boolean;
  comment: string;
}