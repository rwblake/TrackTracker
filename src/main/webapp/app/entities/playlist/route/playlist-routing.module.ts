import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PlaylistComponent } from '../list/playlist.component';
import { PlaylistDetailComponent } from '../detail/playlist-detail.component';
import { PlaylistUpdateComponent } from '../update/playlist-update.component';
import { PlaylistRoutingResolveService } from './playlist-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const playlistRoute: Routes = [
  {
    path: '',
    component: PlaylistComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlaylistDetailComponent,
    resolve: {
      playlist: PlaylistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlaylistUpdateComponent,
    resolve: {
      playlist: PlaylistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlaylistUpdateComponent,
    resolve: {
      playlist: PlaylistRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(playlistRoute)],
  exports: [RouterModule],
})
export class PlaylistRoutingModule {}
