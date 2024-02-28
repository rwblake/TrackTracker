import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlaylistInsightsComponent } from './playlist-insights.component';

const routes: Routes = [{ path: '', component: PlaylistInsightsComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PlaylistInsightsRoutingModule {}
