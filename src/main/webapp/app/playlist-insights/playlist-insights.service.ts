import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';
import { PlaylistInsightsResponse } from './playlist-insights-response-interface';

export type EntityResponseType = HttpResponse<String>;

@Injectable({ providedIn: 'root' })
export class PlaylistInsightsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/playlist-insights');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  sendURL(url: String): Observable<PlaylistInsightsResponse> {
    return this.http.post<PlaylistInsightsResponse>(this.resourceUrl, url);
  }
}
