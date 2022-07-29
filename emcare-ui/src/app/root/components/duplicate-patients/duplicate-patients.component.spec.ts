import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DuplicatePatientsComponent } from './duplicate-patients.component';

describe('DuplicatePatientsComponent', () => {
  let component: DuplicatePatientsComponent;
  let fixture: ComponentFixture<DuplicatePatientsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DuplicatePatientsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DuplicatePatientsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
