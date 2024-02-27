import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFriendRequest } from '../friend-request.model';
import { FriendRequestService } from '../service/friend-request.service';

@Injectable({ providedIn: 'root' })
export class FriendRequestRoutingResolveService implements Resolve<IFriendRequest | null> {
  constructor(protected service: FriendRequestService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFriendRequest | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((friendRequest: HttpResponse<IFriendRequest>) => {
          if (friendRequest.body) {
            return of(friendRequest.body);
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
