import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';
import { IPopularCategories } from './friends-insights.model';

@Injectable({
  providedIn: 'root',
})
export class FriendsInsightsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/friends-insights');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  // api/friends-insights/popular-categories
  getPopularCategories(periodDays?: number): Observable<IPopularCategories> {
    if (periodDays !== undefined && periodDays > 0) {
      return this.http.get<IPopularCategories>(this.resourceUrl.concat('/popular-categories'), { params: { period: periodDays } });
    }

    return this.http.get<IPopularCategories>(this.resourceUrl.concat('/popular-categories'));
  }
}
