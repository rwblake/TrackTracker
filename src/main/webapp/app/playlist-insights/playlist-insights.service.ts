import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';
import { PlaylistInsightsResponse } from './playlist-insights-response-interface';
import { IPlaylist } from '../entities/playlist/playlist.model';

export type EntityResponseType = HttpResponse<String>;

@Injectable({ providedIn: 'root' })
export class PlaylistInsightsService {
  protected insightsURL = this.applicationConfigService.getEndpointFor('api/playlist-insights');
  protected playlistsURL = this.applicationConfigService.getEndpointFor('api/current-user-playlists');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  sendURL(url: String): Observable<PlaylistInsightsResponse> {
    return this.http.post<PlaylistInsightsResponse>(this.insightsURL, url);
  }

  retrieveUserPlaylists(): Observable<IPlaylist[]> {
    return this.http.get<IPlaylist[]>(this.playlistsURL);
  }
}
