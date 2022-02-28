import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowFacilityComponent } from './show-facility.component';

describe('ShowFacilityComponent', () => {
  let component: ShowFacilityComponent;
  let fixture: ComponentFixture<ShowFacilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShowFacilityComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowFacilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
