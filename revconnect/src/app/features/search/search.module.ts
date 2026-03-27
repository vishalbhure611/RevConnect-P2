import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { SearchComponent } from './components/search.component';

const routes: Routes = [{ path: '', component: SearchComponent }];

@NgModule({
  declarations: [SearchComponent],
  imports: [SharedModule, RouterModule.forChild(routes)]
})
export class SearchModule {}
