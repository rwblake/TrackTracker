import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStream } from '../stream.model';
import { StreamService } from '../service/stream.service';

@Injectable({ providedIn: 'root' })
export class StreamRoutingResolveService implements Resolve<IStream | null> {
  constructor(protected service: StreamService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStream | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((stream: HttpResponse<IStream>) => {
          if (stream.body) {
            return of(stream.body);
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
