import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SharingPreferenceComponent } from '../list/sharing-preference.component';
import { SharingPreferenceDetailComponent } from '../detail/sharing-preference-detail.component';
import { SharingPreferenceUpdateComponent } from '../update/sharing-preference-update.component';
import { SharingPreferenceRoutingResolveService } from './sharing-preference-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const sharingPreferenceRoute: Routes = [
  {
    path: '',
    component: SharingPreferenceComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SharingPreferenceDetailComponent,
    resolve: {
      sharingPreference: SharingPreferenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SharingPreferenceUpdateComponent,
    resolve: {
      sharingPreference: SharingPreferenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SharingPreferenceUpdateComponent,
    resolve: {
      sharingPreference: SharingPreferenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(sharingPreferenceRoute)],
  exports: [RouterModule],
})
export class SharingPreferenceRoutingModule {}
