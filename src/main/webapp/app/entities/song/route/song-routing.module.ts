import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SongComponent } from '../list/song.component';
import { SongDetailComponent } from '../detail/song-detail.component';
import { SongUpdateComponent } from '../update/song-update.component';
import { SongRoutingResolveService } from './song-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const songRoute: Routes = [
  {
    path: '',
    component: SongComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SongDetailComponent,
    resolve: {
      song: SongRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SongUpdateComponent,
    resolve: {
      song: SongRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SongUpdateComponent,
    resolve: {
      song: SongRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(songRoute)],
  exports: [RouterModule],
})
export class SongRoutingModule {}
