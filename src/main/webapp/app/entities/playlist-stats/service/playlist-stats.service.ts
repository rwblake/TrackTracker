import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlaylistStats, NewPlaylistStats } from '../playlist-stats.model';

export type PartialUpdatePlaylistStats = Partial<IPlaylistStats> & Pick<IPlaylistStats, 'id'>;

type RestOf<T extends IPlaylistStats | NewPlaylistStats> = Omit<T, 'lastUpdated'> & {
  lastUpdated?: string | null;
};

export type RestPlaylistStats = RestOf<IPlaylistStats>;

export type NewRestPlaylistStats = RestOf<NewPlaylistStats>;

export type PartialUpdateRestPlaylistStats = RestOf<PartialUpdatePlaylistStats>;

export type EntityResponseType = HttpResponse<IPlaylistStats>;
export type EntityArrayResponseType = HttpResponse<IPlaylistStats[]>;

@Injectable({ providedIn: 'root' })
export class PlaylistStatsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/playlist-stats');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(playlistStats: NewPlaylistStats): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(playlistStats);
    return this.http
      .post<RestPlaylistStats>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(playlistStats: IPlaylistStats): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(playlistStats);
    return this.http
      .put<RestPlaylistStats>(`${this.resourceUrl}/${this.getPlaylistStatsIdentifier(playlistStats)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(playlistStats: PartialUpdatePlaylistStats): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(playlistStats);
    return this.http
      .patch<RestPlaylistStats>(`${this.resourceUrl}/${this.getPlaylistStatsIdentifier(playlistStats)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPlaylistStats>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPlaylistStats[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPlaylistStatsIdentifier(playlistStats: Pick<IPlaylistStats, 'id'>): number {
    return playlistStats.id;
  }

  comparePlaylistStats(o1: Pick<IPlaylistStats, 'id'> | null, o2: Pick<IPlaylistStats, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlaylistStatsIdentifier(o1) === this.getPlaylistStatsIdentifier(o2) : o1 === o2;
  }

  addPlaylistStatsToCollectionIfMissing<Type extends Pick<IPlaylistStats, 'id'>>(
    playlistStatsCollection: Type[],
    ...playlistStatsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const playlistStats: Type[] = playlistStatsToCheck.filter(isPresent);
    if (playlistStats.length > 0) {
      const playlistStatsCollectionIdentifiers = playlistStatsCollection.map(
        playlistStatsItem => this.getPlaylistStatsIdentifier(playlistStatsItem)!
      );
      const playlistStatsToAdd = playlistStats.filter(playlistStatsItem => {
        const playlistStatsIdentifier = this.getPlaylistStatsIdentifier(playlistStatsItem);
        if (playlistStatsCollectionIdentifiers.includes(playlistStatsIdentifier)) {
          return false;
        }
        playlistStatsCollectionIdentifiers.push(playlistStatsIdentifier);
        return true;
      });
      return [...playlistStatsToAdd, ...playlistStatsCollection];
    }
    return playlistStatsCollection;
  }

  protected convertDateFromClient<T extends IPlaylistStats | NewPlaylistStats | PartialUpdatePlaylistStats>(playlistStats: T): RestOf<T> {
    return {
      ...playlistStats,
      lastUpdated: playlistStats.lastUpdated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPlaylistStats: RestPlaylistStats): IPlaylistStats {
    return {
      ...restPlaylistStats,
      lastUpdated: restPlaylistStats.lastUpdated ? dayjs(restPlaylistStats.lastUpdated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPlaylistStats>): HttpResponse<IPlaylistStats> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPlaylistStats[]>): HttpResponse<IPlaylistStats[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
