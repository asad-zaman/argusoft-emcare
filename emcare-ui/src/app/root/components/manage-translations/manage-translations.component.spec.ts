import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageTranslationsComponent } from './manage-translations.component';

describe('ManageTranslationsComponent', () => {
  let component: ManageTranslationsComponent;
  let fixture: ComponentFixture<ManageTranslationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageTranslationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageTranslationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
