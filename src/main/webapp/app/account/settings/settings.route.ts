import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SettingsComponent } from './settings.component';
import { APP_NAME } from '../../app.constants';

export const settingsRoute: Route = {
  path: 'settings',
  component: SettingsComponent,
  data: {
    pageTitle: APP_NAME + ' - Settings',
  },
  canActivate: [UserRouteAccessService],
};
