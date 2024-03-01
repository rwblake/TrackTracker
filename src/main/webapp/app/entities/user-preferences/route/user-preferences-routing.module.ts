import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserPreferencesComponent } from '../list/user-preferences.component';
import { UserPreferencesDetailComponent } from '../detail/user-preferences-detail.component';
import { UserPreferencesUpdateComponent } from '../update/user-preferences-update.component';
import { UserPreferencesRoutingResolveService } from './user-preferences-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userPreferencesRoute: Routes = [
  {
    path: '',
    component: UserPreferencesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserPreferencesDetailComponent,
    resolve: {
      userPreferences: UserPreferencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserPreferencesUpdateComponent,
    resolve: {
      userPreferences: UserPreferencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserPreferencesUpdateComponent,
    resolve: {
      userPreferences: UserPreferencesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userPreferencesRoute)],
  exports: [RouterModule],
})
export class UserPreferencesRoutingModule {}
