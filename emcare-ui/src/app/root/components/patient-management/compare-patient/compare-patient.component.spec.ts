import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComparePatientComponent } from './compare-patient.component';

describe('ComparePatientComponent', () => {
  let component: ComparePatientComponent;
  let fixture: ComponentFixture<ComparePatientComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ComparePatientComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ComparePatientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});