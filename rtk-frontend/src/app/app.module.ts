import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {HttpClientModule} from '@angular/common/http';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms'

import { TreeModule } from 'angular-tree-component';
import { ProtectedObjectsComponent } from './protected-objects/protected-objects.component';
import { DatesComponent } from './dates/dates.component';
import { ActionsComponent } from './actions/actions.component';
import {BlockUIModule} from "ng-block-ui";
import {ModalDialogModule} from "ngx-modal-dialog";
import { ViewLicenseComponent } from './view-license/view-license.component';

@NgModule({
  declarations: [
    AppComponent,
    ProtectedObjectsComponent,
    DatesComponent,
    ActionsComponent,
    ViewLicenseComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    CommonModule,
    FormsModule,
    TreeModule.forRoot(),
    ReactiveFormsModule,
    BlockUIModule.forRoot(),
    ModalDialogModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent],
  exports: [],
  entryComponents: [ViewLicenseComponent]
})
export class AppModule { }