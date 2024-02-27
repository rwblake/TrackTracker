import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlaylistStats } from '../playlist-stats.model';
import { PlaylistStatsService } from '../service/playlist-stats.service';

@Injectable({ providedIn: 'root' })
export class PlaylistStatsRoutingResolveService implements Resolve<IPlaylistStats | null> {
  constructor(protected service: PlaylistStatsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlaylistStats | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((playlistStats: HttpResponse<IPlaylistStats>) => {
          if (playlistStats.body) {
            return of(playlistStats.body);
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
