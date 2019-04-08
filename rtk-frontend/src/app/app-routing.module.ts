import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { GeneratorComponent } from "./license/generator/generator.component";
import { ViewerComponent } from "./license/viewer/viewer.component";
import { CommonModule } from "@angular/common";
import { FormsModule } from '@angular/forms'


const routes: Routes = [
  { path: "generator", component: GeneratorComponent },
  { path: "viewer", component: ViewerComponent },
  { path: "", component: GeneratorComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes), 
    CommonModule,
    FormsModule
  ],
  exports: [RouterModule],
  declarations: [GeneratorComponent, ViewerComponent]
})
export class AppRoutingModule {}
