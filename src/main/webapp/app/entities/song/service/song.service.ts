import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISong, NewSong } from '../song.model';

export type PartialUpdateSong = Partial<ISong> & Pick<ISong, 'id'>;

type RestOf<T extends ISong | NewSong> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

export type RestSong = RestOf<ISong>;

export type NewRestSong = RestOf<NewSong>;

export type PartialUpdateRestSong = RestOf<PartialUpdateSong>;

export type EntityResponseType = HttpResponse<ISong>;
export type EntityArrayResponseType = HttpResponse<ISong[]>;

@Injectable({ providedIn: 'root' })
export class SongService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/songs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(song: NewSong): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(song);
    return this.http.post<RestSong>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(song: ISong): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(song);
    return this.http
      .put<RestSong>(`${this.resourceUrl}/${this.getSongIdentifier(song)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(song: PartialUpdateSong): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(song);
    return this.http
      .patch<RestSong>(`${this.resourceUrl}/${this.getSongIdentifier(song)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSong>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSong[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSongIdentifier(song: Pick<ISong, 'id'>): number {
    return song.id;
  }

  compareSong(o1: Pick<ISong, 'id'> | null, o2: Pick<ISong, 'id'> | null): boolean {
    return o1 && o2 ? this.getSongIdentifier(o1) === this.getSongIdentifier(o2) : o1 === o2;
  }

  addSongToCollectionIfMissing<Type extends Pick<ISong, 'id'>>(
    songCollection: Type[],
    ...songsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const songs: Type[] = songsToCheck.filter(isPresent);
    if (songs.length > 0) {
      const songCollectionIdentifiers = songCollection.map(songItem => this.getSongIdentifier(songItem)!);
      const songsToAdd = songs.filter(songItem => {
        const songIdentifier = this.getSongIdentifier(songItem);
        if (songCollectionIdentifiers.includes(songIdentifier)) {
          return false;
        }
        songCollectionIdentifiers.push(songIdentifier);
        return true;
      });
      return [...songsToAdd, ...songCollection];
    }
    return songCollection;
  }

  protected convertDateFromClient<T extends ISong | NewSong | PartialUpdateSong>(song: T): RestOf<T> {
    return {
      ...song,
      releaseDate: song.releaseDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSong: RestSong): ISong {
    return {
      ...restSong,
      releaseDate: restSong.releaseDate ? dayjs(restSong.releaseDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSong>): HttpResponse<ISong> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSong[]>): HttpResponse<ISong[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
