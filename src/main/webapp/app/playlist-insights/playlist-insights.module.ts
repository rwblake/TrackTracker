import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PlaylistInsightsRoutingModule } from './playlist-insights-routing.module';
import { PlaylistInsightsComponent } from './playlist-insights.component';

@NgModule({
  declarations: [PlaylistInsightsComponent],
  imports: [CommonModule, PlaylistInsightsRoutingModule],
})
export class PlaylistInsightsModule {}
