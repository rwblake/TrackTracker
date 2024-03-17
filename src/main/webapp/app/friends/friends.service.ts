import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';
import { IFriendship } from 'app/entities/friendship/friendship.model';
import { IFriendRequest } from '../entities/friend-request/friend-request.model';
import { IFriend } from './friend.model';
import { IAppUser } from '../entities/app-user/app-user.model';

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

  getUsers(): Observable<IAppUser[]> {
    return this.http.get<IAppUser[]>(this.resourceUrl + '/appUsers');
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

  delete(friend: IFriend): Observable<HttpResponse<{}>> {
    return this.http.post(this.resourceUrl + '/delete', friend.friendAppUser?.id, { observe: 'response' });
  }
}
