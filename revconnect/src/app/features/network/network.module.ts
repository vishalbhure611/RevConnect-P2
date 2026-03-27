import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { NetworkComponent } from './components/network.component';

const routes: Routes = [{ path: '', component: NetworkComponent }];

@NgModule({
  declarations: [NetworkComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class NetworkModule {}
