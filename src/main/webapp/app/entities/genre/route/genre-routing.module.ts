import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GenreComponent } from '../list/genre.component';
import { GenreDetailComponent } from '../detail/genre-detail.component';
import { GenreUpdateComponent } from '../update/genre-update.component';
import { GenreRoutingResolveService } from './genre-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const genreRoute: Routes = [
  {
    path: '',
    component: GenreComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GenreDetailComponent,
    resolve: {
      genre: GenreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GenreUpdateComponent,
    resolve: {
      genre: GenreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GenreUpdateComponent,
    resolve: {
      genre: GenreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(genreRoute)],
  exports: [RouterModule],
})
export class GenreRoutingModule {}
