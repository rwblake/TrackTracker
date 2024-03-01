import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFeed } from '../feed.model';
import { FeedService } from '../service/feed.service';

@Injectable({ providedIn: 'root' })
export class FeedRoutingResolveService implements Resolve<IFeed | null> {
  constructor(protected service: FeedService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFeed | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((feed: HttpResponse<IFeed>) => {
          if (feed.body) {
            return of(feed.body);
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
