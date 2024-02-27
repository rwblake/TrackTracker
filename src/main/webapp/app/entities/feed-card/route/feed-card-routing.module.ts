import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FeedCardComponent } from '../list/feed-card.component';
import { FeedCardDetailComponent } from '../detail/feed-card-detail.component';
import { FeedCardUpdateComponent } from '../update/feed-card-update.component';
import { FeedCardRoutingResolveService } from './feed-card-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const feedCardRoute: Routes = [
  {
    path: '',
    component: FeedCardComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FeedCardDetailComponent,
    resolve: {
      feedCard: FeedCardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FeedCardUpdateComponent,
    resolve: {
      feedCard: FeedCardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FeedCardUpdateComponent,
    resolve: {
      feedCard: FeedCardRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(feedCardRoute)],
  exports: [RouterModule],
})
export class FeedCardRoutingModule {}
