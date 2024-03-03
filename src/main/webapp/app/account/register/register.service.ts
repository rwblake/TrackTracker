import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Registration } from './register.model';
import { IAlbum } from '../../entities/album/album.model';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';
import { RestAlbum } from '../../entities/album/service/album.service';

@Injectable({ providedIn: 'root' })
export class RegisterService {
  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  save(registration: Registration): Observable<{}> {
    return this.http.post(this.applicationConfigService.getEndpointFor('api/register'), registration);
  }

  getAuthenticationURI(): Observable<HttpResponse<URL>> {
    return this.http.get<URL>(this.applicationConfigService.getEndpointFor('api/spotify/authentication_uri'), { observe: 'response' });
  }
}
