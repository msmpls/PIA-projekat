export interface CourtRequest {
  court_name: string;
  sport_type: string;
  type: 'OPENED' | 'CLOSED';
  capacity: number;
  equipment_description: string;
}

export interface FacilityRequest {
  name: string;
  city: string;
  address: string;
  description: string;
  price_per_hour: number;
  type: string;
  status?: string;
  work_start_time: string;
  work_end_time: string;
  max_no_shows: number;
  company_name: string;
  company_address: string;
  company_id_number: string;
  pib: string;
  courts: CourtRequest[];
}

export interface Facility {
  id: number;
  name: string;
  city: string;
  address: string;
  description: string;
  price_per_hour: number;
  type: string;
  status: string;
  created_by: number;
  company_name: string;
  company_address: string;
  company_id_number: string;
  pib: string;
  work_start_time: string;
  work_end_time: string;
  sports?: string; // GROUP_CONCAT iz bekenda (za pretragu)
  likes?: number;  // Za top 3 na homepage
}
