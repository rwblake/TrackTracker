import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FriendsInsightsComponent } from './friends-insights.component';

const routes: Routes = [{ path: '', component: FriendsInsightsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FriendsInsightsRoutingModule {}
