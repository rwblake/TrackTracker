import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFeedCard } from '../feed-card.model';
import { FeedCardService } from '../service/feed-card.service';

@Injectable({ providedIn: 'root' })
export class FeedCardRoutingResolveService implements Resolve<IFeedCard | null> {
  constructor(protected service: FeedCardService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFeedCard | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((feedCard: HttpResponse<IFeedCard>) => {
          if (feedCard.body) {
            return of(feedCard.body);
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
