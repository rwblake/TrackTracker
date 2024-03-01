import { Route } from '@angular/router';

import { HomeComponent } from './home.component';

import { APP_NAME } from '../app.constants';

export const HOME_ROUTE: Route = {
  path: '',
  component: HomeComponent,
  data: {
    pageTitle: APP_NAME + ' - Home',
  },
};
