import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';
import { IFriendship } from 'app/entities/friendship/friendship.model';
import { IFriendRequest } from '../entities/friend-request/friend-request.model';
import { IFriend } from './friend.model';

export type EntityResponseType = HttpResponse<String>;
export type EntityArrayResponseType = HttpResponse<IFriendRequest>;

@Injectable({ providedIn: 'root' })
export class FriendsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friends');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  sendURL(id: number): Observable<IFriendship> {
    return this.http.post<IFriendship>(this.resourceUrl, id);
  }

  getFriendRequests(): Observable<IFriendRequest[]> {
    return this.http.get<IFriendRequest[]>(this.resourceUrl + '/requests');
  }

  getFriends(): Observable<IFriend[]> {
    return this.http.get<IFriend[]>(this.resourceUrl);
  }

  acceptFriendRequest(id: number): Observable<IFriendRequest[]> {
    return this.http.post<IFriendRequest[]>(this.resourceUrl + '/accept', id);
  }

  rejectFriendRequest(id: number): Observable<IFriendRequest[]> {
    return this.http.post<IFriendRequest[]>(this.resourceUrl + '/reject', id);
  }

  pin(id: number) {
    this.http.post(this.resourceUrl + '/pin', id);
  }

  unpin(id: number) {
    this.http.post(this.resourceUrl + '/unpin', id);
  }
}
