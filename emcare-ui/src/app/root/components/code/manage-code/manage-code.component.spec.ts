import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageCodeComponent } from './manage-code.component';

describe('ManageCodeComponent', () => {
  let component: ManageCodeComponent;
  let fixture: ComponentFixture<ManageCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageCodeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
