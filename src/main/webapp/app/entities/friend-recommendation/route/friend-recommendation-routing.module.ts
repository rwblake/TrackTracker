import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FriendRecommendationComponent } from '../list/friend-recommendation.component';
import { FriendRecommendationDetailComponent } from '../detail/friend-recommendation-detail.component';
import { FriendRecommendationUpdateComponent } from '../update/friend-recommendation-update.component';
import { FriendRecommendationRoutingResolveService } from './friend-recommendation-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const friendRecommendationRoute: Routes = [
  {
    path: '',
    component: FriendRecommendationComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FriendRecommendationDetailComponent,
    resolve: {
      friendRecommendation: FriendRecommendationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FriendRecommendationUpdateComponent,
    resolve: {
      friendRecommendation: FriendRecommendationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FriendRecommendationUpdateComponent,
    resolve: {
      friendRecommendation: FriendRecommendationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(friendRecommendationRoute)],
  exports: [RouterModule],
})
export class FriendRecommendationRoutingModule {}
