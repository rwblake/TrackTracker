import { Route } from '@angular/router';

import { RegisterComponent } from './register.component';
import { APP_NAME } from '../../app.constants';

export const registerRoute: Route = {
  path: 'register',
  component: RegisterComponent,
  data: {
    pageTitle: APP_NAME + ' - Create Account',
  },
};
