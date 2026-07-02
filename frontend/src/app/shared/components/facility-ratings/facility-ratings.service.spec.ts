import { TestBed } from '@angular/core/testing';

import { FacilityRatingsService } from './facility-ratings.service';

describe('FacilityRatingsService', () => {
  let service: FacilityRatingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FacilityRatingsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
