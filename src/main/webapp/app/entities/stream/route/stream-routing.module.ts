import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StreamComponent } from '../list/stream.component';
import { StreamDetailComponent } from '../detail/stream-detail.component';
import { StreamUpdateComponent } from '../update/stream-update.component';
import { StreamRoutingResolveService } from './stream-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const streamRoute: Routes = [
  {
    path: '',
    component: StreamComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StreamDetailComponent,
    resolve: {
      stream: StreamRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StreamUpdateComponent,
    resolve: {
      stream: StreamRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StreamUpdateComponent,
    resolve: {
      stream: StreamRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(streamRoute)],
  exports: [RouterModule],
})
export class StreamRoutingModule {}
