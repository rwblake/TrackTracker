import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFriendship, NewFriendship } from '../friendship.model';

export type PartialUpdateFriendship = Partial<IFriendship> & Pick<IFriendship, 'id'>;

type RestOf<T extends IFriendship | NewFriendship> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestFriendship = RestOf<IFriendship>;

export type NewRestFriendship = RestOf<NewFriendship>;

export type PartialUpdateRestFriendship = RestOf<PartialUpdateFriendship>;

export type EntityResponseType = HttpResponse<IFriendship>;
export type EntityArrayResponseType = HttpResponse<IFriendship[]>;

@Injectable({ providedIn: 'root' })
export class FriendshipService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friendships');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(friendship: NewFriendship): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendship);
    return this.http
      .post<RestFriendship>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(friendship: IFriendship): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendship);
    return this.http
      .put<RestFriendship>(`${this.resourceUrl}/${this.getFriendshipIdentifier(friendship)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(friendship: PartialUpdateFriendship): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendship);
    return this.http
      .patch<RestFriendship>(`${this.resourceUrl}/${this.getFriendshipIdentifier(friendship)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFriendship>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFriendship[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFriendshipIdentifier(friendship: Pick<IFriendship, 'id'>): number {
    return friendship.id;
  }

  compareFriendship(o1: Pick<IFriendship, 'id'> | null, o2: Pick<IFriendship, 'id'> | null): boolean {
    return o1 && o2 ? this.getFriendshipIdentifier(o1) === this.getFriendshipIdentifier(o2) : o1 === o2;
  }

  addFriendshipToCollectionIfMissing<Type extends Pick<IFriendship, 'id'>>(
    friendshipCollection: Type[],
    ...friendshipsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const friendships: Type[] = friendshipsToCheck.filter(isPresent);
    if (friendships.length > 0) {
      const friendshipCollectionIdentifiers = friendshipCollection.map(friendshipItem => this.getFriendshipIdentifier(friendshipItem)!);
      const friendshipsToAdd = friendships.filter(friendshipItem => {
        const friendshipIdentifier = this.getFriendshipIdentifier(friendshipItem);
        if (friendshipCollectionIdentifiers.includes(friendshipIdentifier)) {
          return false;
        }
        friendshipCollectionIdentifiers.push(friendshipIdentifier);
        return true;
      });
      return [...friendshipsToAdd, ...friendshipCollection];
    }
    return friendshipCollection;
  }

  protected convertDateFromClient<T extends IFriendship | NewFriendship | PartialUpdateFriendship>(friendship: T): RestOf<T> {
    return {
      ...friendship,
      createdAt: friendship.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFriendship: RestFriendship): IFriendship {
    return {
      ...restFriendship,
      createdAt: restFriendship.createdAt ? dayjs(restFriendship.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFriendship>): HttpResponse<IFriendship> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFriendship[]>): HttpResponse<IFriendship[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
