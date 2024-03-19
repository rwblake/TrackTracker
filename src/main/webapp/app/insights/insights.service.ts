import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { AccountService } from '../core/auth/account.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Observable } from 'rxjs';
import { InsightsResponse, StreamInsightsResponse } from './insights-response-interface';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';
import { IStream } from '../entities/stream/stream.model';

// export type EntityResponseType = HttpResponse<String>;

@Injectable({ providedIn: 'root' })
export class InsightsService {
  protected streamsURL: string = this.applicationConfigService.getEndpointFor('api/insights');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  // sendResponse(): Observable<InsightsResponse>{
  //   return this.http.post<InsightsResponse>(this.insightsURL);
  // }

  retrieveStreamInsights(): Observable<StreamInsightsResponse> {
    return this.http.get<StreamInsightsResponse>(this.streamsURL);
  }
}
