import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GeneratorComponent } from './license/generator/generator.component';
import { ViewerComponent } from './license/viewer/viewer.component';

const routes: Routes = [
  { path: 'generator', component: GeneratorComponent },
  { path: 'viewer', component: ViewerComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  declarations: [GeneratorComponent]
})
export class AppRoutingModule { }
