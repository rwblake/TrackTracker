import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CardTemplateComponent } from '../list/card-template.component';
import { CardTemplateDetailComponent } from '../detail/card-template-detail.component';
import { CardTemplateUpdateComponent } from '../update/card-template-update.component';
import { CardTemplateRoutingResolveService } from './card-template-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const cardTemplateRoute: Routes = [
  {
    path: '',
    component: CardTemplateComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CardTemplateDetailComponent,
    resolve: {
      cardTemplate: CardTemplateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CardTemplateUpdateComponent,
    resolve: {
      cardTemplate: CardTemplateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CardTemplateUpdateComponent,
    resolve: {
      cardTemplate: CardTemplateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cardTemplateRoute)],
  exports: [RouterModule],
})
export class CardTemplateRoutingModule {}
