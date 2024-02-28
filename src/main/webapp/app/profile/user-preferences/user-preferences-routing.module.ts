import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserPreferencesComponent } from './user-preferences.component';

const routes: Routes = [{ path: '', component: UserPreferencesComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserPreferencesRoutingModule {}
