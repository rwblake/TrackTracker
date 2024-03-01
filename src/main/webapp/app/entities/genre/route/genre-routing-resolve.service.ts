import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGenre } from '../genre.model';
import { GenreService } from '../service/genre.service';

@Injectable({ providedIn: 'root' })
export class GenreRoutingResolveService implements Resolve<IGenre | null> {
  constructor(protected service: GenreService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGenre | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((genre: HttpResponse<IGenre>) => {
          if (genre.body) {
            return of(genre.body);
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
