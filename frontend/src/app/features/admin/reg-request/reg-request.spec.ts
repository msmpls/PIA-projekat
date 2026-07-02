import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegRequest } from './reg-request';

describe('RegRequest', () => {
  let component: RegRequest;
  let fixture: ComponentFixture<RegRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
