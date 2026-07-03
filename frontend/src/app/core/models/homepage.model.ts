import { Facility } from "./facility.model";
import { PromotionSummary } from "./promotion.model";

// CAMELCASE
export interface HomepageResponse {
  totalFacilities: number;
  topFacilities: Facility[]; // Map<String, Object> sa bekenda - nestrukturirano
  promotions: PromotionSummary[];
}