import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICardTemplate } from '../card-template.model';
import { CardTemplateService } from '../service/card-template.service';

@Injectable({ providedIn: 'root' })
export class CardTemplateRoutingResolveService implements Resolve<ICardTemplate | null> {
  constructor(protected service: CardTemplateService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICardTemplate | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cardTemplate: HttpResponse<ICardTemplate>) => {
          if (cardTemplate.body) {
            return of(cardTemplate.body);
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
