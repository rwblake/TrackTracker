import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PlaylistInsightsRoutingModule } from './playlist-insights-routing.module';
import { PlaylistInsightsComponent } from './playlist-insights.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { LegendPosition, NgxChartsModule, PieChartModule } from '@swimlane/ngx-charts';

@NgModule({
  declarations: [PlaylistInsightsComponent],
  imports: [
    CommonModule,
    PlaylistInsightsRoutingModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    NgxChartsModule,
    SharedModule,
    PieChartModule,
  ],
})
export class PlaylistInsightsModule {}
