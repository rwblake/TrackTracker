import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpotifyToken } from '../spotify-token.model';
import { SpotifyTokenService } from '../service/spotify-token.service';

@Injectable({ providedIn: 'root' })
export class SpotifyTokenRoutingResolveService implements Resolve<ISpotifyToken | null> {
  constructor(protected service: SpotifyTokenService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISpotifyToken | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((spotifyToken: HttpResponse<ISpotifyToken>) => {
          if (spotifyToken.body) {
            return of(spotifyToken.body);
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
