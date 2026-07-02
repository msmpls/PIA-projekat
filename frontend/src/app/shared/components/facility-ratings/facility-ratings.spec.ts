import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FacilityRatings } from './facility-ratings';

describe('FacilityRatings', () => {
  let component: FacilityRatings;
  let fixture: ComponentFixture<FacilityRatings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityRatings]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FacilityRatings);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
