import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SpotifyTokenComponent } from '../list/spotify-token.component';
import { SpotifyTokenDetailComponent } from '../detail/spotify-token-detail.component';
import { SpotifyTokenUpdateComponent } from '../update/spotify-token-update.component';
import { SpotifyTokenRoutingResolveService } from './spotify-token-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const spotifyTokenRoute: Routes = [
  {
    path: '',
    component: SpotifyTokenComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SpotifyTokenDetailComponent,
    resolve: {
      spotifyToken: SpotifyTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SpotifyTokenUpdateComponent,
    resolve: {
      spotifyToken: SpotifyTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SpotifyTokenUpdateComponent,
    resolve: {
      spotifyToken: SpotifyTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(spotifyTokenRoute)],
  exports: [RouterModule],
})
export class SpotifyTokenRoutingModule {}
