export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  first_name: string;
  last_name: string;
  phone: string;
  role: 'ATHLETE' | 'EMPLOYEE';
  favorite_sports: string;       // verovatno comma-separated string
  // profile_image: File -- ide kroz FormData, ne u JSON telo
  facility_name?: string;        // samo za EMPLOYEE
  company_address?: string;      // samo za EMPLOYEE
  company_id_number?: string;    // samo za EMPLOYEE, 8 cifara
  pib?: string;                  // samo za EMPLOYEE, 9 cifara
}

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
  status: string; // PENDING | APPROVED | REJECTED -- bitno za login flow
}