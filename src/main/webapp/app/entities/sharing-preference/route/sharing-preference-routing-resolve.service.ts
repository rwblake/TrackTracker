import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISharingPreference } from '../sharing-preference.model';
import { SharingPreferenceService } from '../service/sharing-preference.service';

@Injectable({ providedIn: 'root' })
export class SharingPreferenceRoutingResolveService implements Resolve<ISharingPreference | null> {
  constructor(protected service: SharingPreferenceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISharingPreference | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((sharingPreference: HttpResponse<ISharingPreference>) => {
          if (sharingPreference.body) {
            return of(sharingPreference.body);
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
