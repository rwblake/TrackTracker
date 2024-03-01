import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICardMetric } from '../card-metric.model';
import { CardMetricService } from '../service/card-metric.service';

@Injectable({ providedIn: 'root' })
export class CardMetricRoutingResolveService implements Resolve<ICardMetric | null> {
  constructor(protected service: CardMetricService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICardMetric | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cardMetric: HttpResponse<ICardMetric>) => {
          if (cardMetric.body) {
            return of(cardMetric.body);
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
