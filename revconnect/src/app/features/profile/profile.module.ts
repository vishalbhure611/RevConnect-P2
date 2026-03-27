import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { ProfileComponent } from './components/profile.component';

const routes: Routes = [
  { path: ':id', component: ProfileComponent }
];

@NgModule({
  declarations: [ProfileComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class ProfileModule {}
