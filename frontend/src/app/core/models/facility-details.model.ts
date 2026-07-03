import { Facility } from './facility.model';
import { Court } from './court.model';

export interface FacilityDetails {
  facility: Facility;
  courts: Court[];
  likes: number;
  dislikes: number;
  galleryImages: string[];
}
