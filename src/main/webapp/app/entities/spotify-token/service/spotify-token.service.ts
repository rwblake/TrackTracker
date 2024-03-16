import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpotifyToken, NewSpotifyToken } from '../spotify-token.model';

export type PartialUpdateSpotifyToken = Partial<ISpotifyToken> & Pick<ISpotifyToken, 'id'>;

type RestOf<T extends ISpotifyToken | NewSpotifyToken> = Omit<T, 'expires'> & {
  expires?: string | null;
};

export type RestSpotifyToken = RestOf<ISpotifyToken>;

export type NewRestSpotifyToken = RestOf<NewSpotifyToken>;

export type PartialUpdateRestSpotifyToken = RestOf<PartialUpdateSpotifyToken>;

export type EntityResponseType = HttpResponse<ISpotifyToken>;
export type EntityArrayResponseType = HttpResponse<ISpotifyToken[]>;

@Injectable({ providedIn: 'root' })
export class SpotifyTokenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/spotify-tokens');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(spotifyToken: NewSpotifyToken): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spotifyToken);
    return this.http
      .post<RestSpotifyToken>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(spotifyToken: ISpotifyToken): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spotifyToken);
    return this.http
      .put<RestSpotifyToken>(`${this.resourceUrl}/${this.getSpotifyTokenIdentifier(spotifyToken)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(spotifyToken: PartialUpdateSpotifyToken): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spotifyToken);
    return this.http
      .patch<RestSpotifyToken>(`${this.resourceUrl}/${this.getSpotifyTokenIdentifier(spotifyToken)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSpotifyToken>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSpotifyToken[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSpotifyTokenIdentifier(spotifyToken: Pick<ISpotifyToken, 'id'>): number {
    return spotifyToken.id;
  }

  compareSpotifyToken(o1: Pick<ISpotifyToken, 'id'> | null, o2: Pick<ISpotifyToken, 'id'> | null): boolean {
    return o1 && o2 ? this.getSpotifyTokenIdentifier(o1) === this.getSpotifyTokenIdentifier(o2) : o1 === o2;
  }

  addSpotifyTokenToCollectionIfMissing<Type extends Pick<ISpotifyToken, 'id'>>(
    spotifyTokenCollection: Type[],
    ...spotifyTokensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const spotifyTokens: Type[] = spotifyTokensToCheck.filter(isPresent);
    if (spotifyTokens.length > 0) {
      const spotifyTokenCollectionIdentifiers = spotifyTokenCollection.map(
        spotifyTokenItem => this.getSpotifyTokenIdentifier(spotifyTokenItem)!
      );
      const spotifyTokensToAdd = spotifyTokens.filter(spotifyTokenItem => {
        const spotifyTokenIdentifier = this.getSpotifyTokenIdentifier(spotifyTokenItem);
        if (spotifyTokenCollectionIdentifiers.includes(spotifyTokenIdentifier)) {
          return false;
        }
        spotifyTokenCollectionIdentifiers.push(spotifyTokenIdentifier);
        return true;
      });
      return [...spotifyTokensToAdd, ...spotifyTokenCollection];
    }
    return spotifyTokenCollection;
  }

  protected convertDateFromClient<T extends ISpotifyToken | NewSpotifyToken | PartialUpdateSpotifyToken>(spotifyToken: T): RestOf<T> {
    return {
      ...spotifyToken,
      expires: spotifyToken.expires?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSpotifyToken: RestSpotifyToken): ISpotifyToken {
    return {
      ...restSpotifyToken,
      expires: restSpotifyToken.expires ? dayjs(restSpotifyToken.expires) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSpotifyToken>): HttpResponse<ISpotifyToken> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSpotifyToken[]>): HttpResponse<ISpotifyToken[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
