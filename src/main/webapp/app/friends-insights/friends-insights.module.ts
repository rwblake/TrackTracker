import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FriendsInsightsRoutingModule } from './friends-insights-routing.module';
import { FriendsInsightsComponent } from './friends-insights.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [FriendsInsightsComponent],
  imports: [CommonModule, FriendsInsightsRoutingModule, SharedModule],
})
export class FriendsInsightsModule {}
