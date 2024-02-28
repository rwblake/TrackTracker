import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FriendsInsightsRoutingModule } from './friends-insights-routing.module';
import { FriendsInsightsComponent } from './friends-insights.component';

@NgModule({
  declarations: [FriendsInsightsComponent],
  imports: [CommonModule, FriendsInsightsRoutingModule],
})
export class FriendsInsightsModule {}
