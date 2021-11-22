import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowLocationTypeComponent } from './show-location-type.component';

describe('ShowLocationTypeComponent', () => {
  let component: ShowLocationTypeComponent;
  let fixture: ComponentFixture<ShowLocationTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShowLocationTypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowLocationTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
