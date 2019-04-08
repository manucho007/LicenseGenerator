import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import {Component, Input} from "@angular/core";

@Component({selector: 'app-protected-objects', template: ''})
class ProtectedObjectsStubComponent {}

@Component({selector: 'app-dates', template: ''})
class DatesStubComponent {}

@Component({selector: 'app-actions', template: ''})
class ActionsStubComponent {
  @Input() dates;
  @Input() protectedObjects;
}

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponent,
        ProtectedObjectsStubComponent,
        DatesStubComponent,
        ActionsStubComponent
      ],
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'rtk-frontend'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('rtk-frontend');
  });

  // it('should render title in a h1 tag', () => {
  //   const fixture = TestBed.createComponent(AppComponent);
  //   fixture.detectChanges();
  //   const compiled = fixture.debugElement.nativeElement;
  //   expect(compiled.querySelector('h1').textContent).toContain('Welcome to rtk-frontend!');
  // });
});
