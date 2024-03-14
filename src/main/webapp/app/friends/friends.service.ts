import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';
import { IFriendship } from 'app/entities/friendship/friendship.model';

export type EntityResponseType = HttpResponse<String>;

@Injectable({ providedIn: 'root' })
export class FriendsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friends');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  sendURL(id: number): Observable<IFriendship> {
    return this.http.post<IFriendship>(this.resourceUrl, id);
  }
}
