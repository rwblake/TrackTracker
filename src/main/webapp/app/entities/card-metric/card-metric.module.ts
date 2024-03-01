import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CardMetricComponent } from './list/card-metric.component';
import { CardMetricDetailComponent } from './detail/card-metric-detail.component';
import { CardMetricUpdateComponent } from './update/card-metric-update.component';
import { CardMetricDeleteDialogComponent } from './delete/card-metric-delete-dialog.component';
import { CardMetricRoutingModule } from './route/card-metric-routing.module';

@NgModule({
  imports: [SharedModule, CardMetricRoutingModule],
  declarations: [CardMetricComponent, CardMetricDetailComponent, CardMetricUpdateComponent, CardMetricDeleteDialogComponent],
})
export class CardMetricModule {}
