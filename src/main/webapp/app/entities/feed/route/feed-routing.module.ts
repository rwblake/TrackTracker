import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FeedComponent } from '../list/feed.component';
import { FeedDetailComponent } from '../detail/feed-detail.component';
import { FeedUpdateComponent } from '../update/feed-update.component';
import { FeedRoutingResolveService } from './feed-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const feedRoute: Routes = [
  {
    path: '',
    component: FeedComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FeedDetailComponent,
    resolve: {
      feed: FeedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FeedUpdateComponent,
    resolve: {
      feed: FeedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FeedUpdateComponent,
    resolve: {
      feed: FeedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(feedRoute)],
  exports: [RouterModule],
})
export class FeedRoutingModule {}
