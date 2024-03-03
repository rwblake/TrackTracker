import { NgModule, ViewContainerRef } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InsightsRoutingModule } from './insights-routing.module';
import { InsightsComponent } from './insights.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [InsightsComponent],
  imports: [CommonModule, InsightsRoutingModule, NgxChartsModule, SharedModule],
})
export class InsightsModule {}
