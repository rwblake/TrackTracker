import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFriendRecommendation } from '../friend-recommendation.model';
import { FriendRecommendationService } from '../service/friend-recommendation.service';

@Injectable({ providedIn: 'root' })
export class FriendRecommendationRoutingResolveService implements Resolve<IFriendRecommendation | null> {
  constructor(protected service: FriendRecommendationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFriendRecommendation | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((friendRecommendation: HttpResponse<IFriendRecommendation>) => {
          if (friendRecommendation.body) {
            return of(friendRecommendation.body);
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
