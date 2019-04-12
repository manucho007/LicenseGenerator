import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProtectedObjectsComponent } from './protected-objects.component';
import {Component, Input} from "@angular/core";
import {HttpClientTestingModule} from "@angular/common/http/testing";

@Component({selector: 'tree-root', template: ''})
class TreeStubComponent {
  @Input() nodes;
  @Input() options;
}

describe('ProtectedObjectsComponent', () => {
  let component: ProtectedObjectsComponent;
  let fixture: ComponentFixture<ProtectedObjectsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ ProtectedObjectsComponent, TreeStubComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProtectedObjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
