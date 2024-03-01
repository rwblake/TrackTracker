import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserPreferences } from '../user-preferences.model';
import { UserPreferencesService } from '../service/user-preferences.service';

@Injectable({ providedIn: 'root' })
export class UserPreferencesRoutingResolveService implements Resolve<IUserPreferences | null> {
  constructor(protected service: UserPreferencesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserPreferences | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userPreferences: HttpResponse<IUserPreferences>) => {
          if (userPreferences.body) {
            return of(userPreferences.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
