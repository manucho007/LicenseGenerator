import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewLicenseComponent } from './view-license.component';

describe('ViewLicenseComponent', () => {
  let component: ViewLicenseComponent;
  let fixture: ComponentFixture<ViewLicenseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewLicenseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewLicenseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
