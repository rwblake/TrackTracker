import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CardMetricComponent } from '../list/card-metric.component';
import { CardMetricDetailComponent } from '../detail/card-metric-detail.component';
import { CardMetricUpdateComponent } from '../update/card-metric-update.component';
import { CardMetricRoutingResolveService } from './card-metric-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const cardMetricRoute: Routes = [
  {
    path: '',
    component: CardMetricComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CardMetricDetailComponent,
    resolve: {
      cardMetric: CardMetricRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CardMetricUpdateComponent,
    resolve: {
      cardMetric: CardMetricRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CardMetricUpdateComponent,
    resolve: {
      cardMetric: CardMetricRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cardMetricRoute)],
  exports: [RouterModule],
})
export class CardMetricRoutingModule {}
