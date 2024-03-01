import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PlaylistStatsComponent } from '../list/playlist-stats.component';
import { PlaylistStatsDetailComponent } from '../detail/playlist-stats-detail.component';
import { PlaylistStatsUpdateComponent } from '../update/playlist-stats-update.component';
import { PlaylistStatsRoutingResolveService } from './playlist-stats-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const playlistStatsRoute: Routes = [
  {
    path: '',
    component: PlaylistStatsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlaylistStatsDetailComponent,
    resolve: {
      playlistStats: PlaylistStatsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlaylistStatsUpdateComponent,
    resolve: {
      playlistStats: PlaylistStatsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlaylistStatsUpdateComponent,
    resolve: {
      playlistStats: PlaylistStatsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(playlistStatsRoute)],
  exports: [RouterModule],
})
export class PlaylistStatsRoutingModule {}
