import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlaylist, NewPlaylist } from '../playlist.model';

export type PartialUpdatePlaylist = Partial<IPlaylist> & Pick<IPlaylist, 'id'>;

export type EntityResponseType = HttpResponse<IPlaylist>;
export type EntityArrayResponseType = HttpResponse<IPlaylist[]>;

@Injectable({ providedIn: 'root' })
export class PlaylistService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/playlists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(playlist: NewPlaylist): Observable<EntityResponseType> {
    return this.http.post<IPlaylist>(this.resourceUrl, playlist, { observe: 'response' });
  }

  update(playlist: IPlaylist): Observable<EntityResponseType> {
    return this.http.put<IPlaylist>(`${this.resourceUrl}/${this.getPlaylistIdentifier(playlist)}`, playlist, { observe: 'response' });
  }

  partialUpdate(playlist: PartialUpdatePlaylist): Observable<EntityResponseType> {
    return this.http.patch<IPlaylist>(`${this.resourceUrl}/${this.getPlaylistIdentifier(playlist)}`, playlist, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlaylist>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlaylist[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPlaylistIdentifier(playlist: Pick<IPlaylist, 'id'>): number {
    return playlist.id;
  }

  comparePlaylist(o1: Pick<IPlaylist, 'id'> | null, o2: Pick<IPlaylist, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlaylistIdentifier(o1) === this.getPlaylistIdentifier(o2) : o1 === o2;
  }

  addPlaylistToCollectionIfMissing<Type extends Pick<IPlaylist, 'id'>>(
    playlistCollection: Type[],
    ...playlistsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const playlists: Type[] = playlistsToCheck.filter(isPresent);
    if (playlists.length > 0) {
      const playlistCollectionIdentifiers = playlistCollection.map(playlistItem => this.getPlaylistIdentifier(playlistItem)!);
      const playlistsToAdd = playlists.filter(playlistItem => {
        const playlistIdentifier = this.getPlaylistIdentifier(playlistItem);
        if (playlistCollectionIdentifiers.includes(playlistIdentifier)) {
          return false;
        }
        playlistCollectionIdentifiers.push(playlistIdentifier);
        return true;
      });
      return [...playlistsToAdd, ...playlistCollection];
    }
    return playlistCollection;
  }
}
