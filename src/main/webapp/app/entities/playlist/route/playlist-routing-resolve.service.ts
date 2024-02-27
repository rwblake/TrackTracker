import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlaylist } from '../playlist.model';
import { PlaylistService } from '../service/playlist.service';

@Injectable({ providedIn: 'root' })
export class PlaylistRoutingResolveService implements Resolve<IPlaylist | null> {
  constructor(protected service: PlaylistService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlaylist | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((playlist: HttpResponse<IPlaylist>) => {
          if (playlist.body) {
            return of(playlist.body);
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
