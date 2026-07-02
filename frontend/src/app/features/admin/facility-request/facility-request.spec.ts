import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FacilityRequest } from './facility-request';

describe('FacilityRequest', () => {
  let component: FacilityRequest;
  let fixture: ComponentFixture<FacilityRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FacilityRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
