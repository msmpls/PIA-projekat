export interface Court {
  id: number;
  facility_id: number;
  court_name: string;
  sport_type: string;
  type: 'OPENED' | 'CLOSED';
  capacity: number;
  equipment_description: string;
}