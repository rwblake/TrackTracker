import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFriendRequest, NewFriendRequest } from '../friend-request.model';

export type PartialUpdateFriendRequest = Partial<IFriendRequest> & Pick<IFriendRequest, 'id'>;

type RestOf<T extends IFriendRequest | NewFriendRequest> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestFriendRequest = RestOf<IFriendRequest>;

export type NewRestFriendRequest = RestOf<NewFriendRequest>;

export type PartialUpdateRestFriendRequest = RestOf<PartialUpdateFriendRequest>;

export type EntityResponseType = HttpResponse<IFriendRequest>;
export type EntityArrayResponseType = HttpResponse<IFriendRequest[]>;

@Injectable({ providedIn: 'root' })
export class FriendRequestService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friend-requests');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(friendRequest: NewFriendRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendRequest);
    return this.http
      .post<RestFriendRequest>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(friendRequest: IFriendRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendRequest);
    return this.http
      .put<RestFriendRequest>(`${this.resourceUrl}/${this.getFriendRequestIdentifier(friendRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(friendRequest: PartialUpdateFriendRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(friendRequest);
    return this.http
      .patch<RestFriendRequest>(`${this.resourceUrl}/${this.getFriendRequestIdentifier(friendRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFriendRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFriendRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFriendRequestIdentifier(friendRequest: Pick<IFriendRequest, 'id'>): number {
    return friendRequest.id;
  }

  compareFriendRequest(o1: Pick<IFriendRequest, 'id'> | null, o2: Pick<IFriendRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getFriendRequestIdentifier(o1) === this.getFriendRequestIdentifier(o2) : o1 === o2;
  }

  addFriendRequestToCollectionIfMissing<Type extends Pick<IFriendRequest, 'id'>>(
    friendRequestCollection: Type[],
    ...friendRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const friendRequests: Type[] = friendRequestsToCheck.filter(isPresent);
    if (friendRequests.length > 0) {
      const friendRequestCollectionIdentifiers = friendRequestCollection.map(
        friendRequestItem => this.getFriendRequestIdentifier(friendRequestItem)!
      );
      const friendRequestsToAdd = friendRequests.filter(friendRequestItem => {
        const friendRequestIdentifier = this.getFriendRequestIdentifier(friendRequestItem);
        if (friendRequestCollectionIdentifiers.includes(friendRequestIdentifier)) {
          return false;
        }
        friendRequestCollectionIdentifiers.push(friendRequestIdentifier);
        return true;
      });
      return [...friendRequestsToAdd, ...friendRequestCollection];
    }
    return friendRequestCollection;
  }

  protected convertDateFromClient<T extends IFriendRequest | NewFriendRequest | PartialUpdateFriendRequest>(friendRequest: T): RestOf<T> {
    return {
      ...friendRequest,
      createdAt: friendRequest.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFriendRequest: RestFriendRequest): IFriendRequest {
    return {
      ...restFriendRequest,
      createdAt: restFriendRequest.createdAt ? dayjs(restFriendRequest.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFriendRequest>): HttpResponse<IFriendRequest> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFriendRequest[]>): HttpResponse<IFriendRequest[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
