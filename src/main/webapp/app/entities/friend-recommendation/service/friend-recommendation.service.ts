import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFriendRecommendation, NewFriendRecommendation } from '../friend-recommendation.model';

export type PartialUpdateFriendRecommendation = Partial<IFriendRecommendation> & Pick<IFriendRecommendation, 'id'>;

type RestOf<T extends IFriendRecommendation | NewFriendRecommendation> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestFriendRecommendation = RestOf<IFriendRecommendation>;

export type NewRestFriendRecommendation = RestOf<NewFriendRecommendation>;

export type PartialUpdateRestFriendRecommendation = RestOf<PartialUpdateFriendRecommendation>;

export type EntityResponseType = HttpResponse<IFriendRecommendation>;
export type EntityArrayResponseType = HttpResponse<IFriendRecommendation[]>;

@Injectable({ providedIn: 'root' })
export class FriendRecommendationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friend-recommendations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(friendRecommendation: NewFriendRecommendation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendRecommendation);
    return this.http
      .post<RestFriendRecommendation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(friendRecommendation: IFriendRecommendation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendRecommendation);
    return this.http
      .put<RestFriendRecommendation>(`${this.resourceUrl}/${this.getFriendRecommendationIdentifier(friendRecommendation)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(friendRecommendation: PartialUpdateFriendRecommendation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendRecommendation);
    return this.http
      .patch<RestFriendRecommendation>(`${this.resourceUrl}/${this.getFriendRecommendationIdentifier(friendRecommendation)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFriendRecommendation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFriendRecommendation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFriendRecommendationIdentifier(friendRecommendation: Pick<IFriendRecommendation, 'id'>): number {
    return friendRecommendation.id;
  }

  compareFriendRecommendation(o1: Pick<IFriendRecommendation, 'id'> | null, o2: Pick<IFriendRecommendation, 'id'> | null): boolean {
    return o1 && o2 ? this.getFriendRecommendationIdentifier(o1) === this.getFriendRecommendationIdentifier(o2) : o1 === o2;
  }

  addFriendRecommendationToCollectionIfMissing<Type extends Pick<IFriendRecommendation, 'id'>>(
    friendRecommendationCollection: Type[],
    ...friendRecommendationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const friendRecommendations: Type[] = friendRecommendationsToCheck.filter(isPresent);
    if (friendRecommendations.length > 0) {
      const friendRecommendationCollectionIdentifiers = friendRecommendationCollection.map(
        friendRecommendationItem => this.getFriendRecommendationIdentifier(friendRecommendationItem)!
      );
      const friendRecommendationsToAdd = friendRecommendations.filter(friendRecommendationItem => {
        const friendRecommendationIdentifier = this.getFriendRecommendationIdentifier(friendRecommendationItem);
        if (friendRecommendationCollectionIdentifiers.includes(friendRecommendationIdentifier)) {
          return false;
        }
        friendRecommendationCollectionIdentifiers.push(friendRecommendationIdentifier);
        return true;
      });
      return [...friendRecommendationsToAdd, ...friendRecommendationCollection];
    }
    return friendRecommendationCollection;
  }

  protected convertDateFromClient<T extends IFriendRecommendation | NewFriendRecommendation | PartialUpdateFriendRecommendation>(
    friendRecommendation: T
  ): RestOf<T> {
    return {
      ...friendRecommendation,
      createdAt: friendRecommendation.createdAt?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restFriendRecommendation: RestFriendRecommendation): IFriendRecommendation {
    return {
      ...restFriendRecommendation,
      createdAt: restFriendRecommendation.createdAt ? dayjs(restFriendRecommendation.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFriendRecommendation>): HttpResponse<IFriendRecommendation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFriendRecommendation[]>): HttpResponse<IFriendRecommendation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
