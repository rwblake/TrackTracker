import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FriendRequestComponent } from '../list/friend-request.component';
import { FriendRequestDetailComponent } from '../detail/friend-request-detail.component';
import { FriendRequestUpdateComponent } from '../update/friend-request-update.component';
import { FriendRequestRoutingResolveService } from './friend-request-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const friendRequestRoute: Routes = [
  {
    path: '',
    component: FriendRequestComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FriendRequestDetailComponent,
    resolve: {
      friendRequest: FriendRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FriendRequestUpdateComponent,
    resolve: {
      friendRequest: FriendRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FriendRequestUpdateComponent,
    resolve: {
      friendRequest: FriendRequestRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(friendRequestRoute)],
  exports: [RouterModule],
})
export class FriendRequestRoutingModule {}
