import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PlaylistInsightsRoutingModule } from './playlist-insights-routing.module';
import { PlaylistInsightsComponent } from './playlist-insights.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [PlaylistInsightsComponent],
  imports: [CommonModule, PlaylistInsightsRoutingModule, FontAwesomeModule, ReactiveFormsModule, SharedModule],
})
export class PlaylistInsightsModule {}
