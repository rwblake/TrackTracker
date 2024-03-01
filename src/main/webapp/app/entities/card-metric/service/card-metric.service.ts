import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICardMetric, NewCardMetric } from '../card-metric.model';

export type PartialUpdateCardMetric = Partial<ICardMetric> & Pick<ICardMetric, 'id'>;

export type EntityResponseType = HttpResponse<ICardMetric>;
export type EntityArrayResponseType = HttpResponse<ICardMetric[]>;

@Injectable({ providedIn: 'root' })
export class CardMetricService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/card-metrics');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cardMetric: NewCardMetric): Observable<EntityResponseType> {
    return this.http.post<ICardMetric>(this.resourceUrl, cardMetric, { observe: 'response' });
  }

  update(cardMetric: ICardMetric): Observable<EntityResponseType> {
    return this.http.put<ICardMetric>(`${this.resourceUrl}/${this.getCardMetricIdentifier(cardMetric)}`, cardMetric, {
      observe: 'response',
    });
  }

  partialUpdate(cardMetric: PartialUpdateCardMetric): Observable<EntityResponseType> {
    return this.http.patch<ICardMetric>(`${this.resourceUrl}/${this.getCardMetricIdentifier(cardMetric)}`, cardMetric, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICardMetric>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICardMetric[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCardMetricIdentifier(cardMetric: Pick<ICardMetric, 'id'>): number {
    return cardMetric.id;
  }

  compareCardMetric(o1: Pick<ICardMetric, 'id'> | null, o2: Pick<ICardMetric, 'id'> | null): boolean {
    return o1 && o2 ? this.getCardMetricIdentifier(o1) === this.getCardMetricIdentifier(o2) : o1 === o2;
  }

  addCardMetricToCollectionIfMissing<Type extends Pick<ICardMetric, 'id'>>(
    cardMetricCollection: Type[],
    ...cardMetricsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cardMetrics: Type[] = cardMetricsToCheck.filter(isPresent);
    if (cardMetrics.length > 0) {
      const cardMetricCollectionIdentifiers = cardMetricCollection.map(cardMetricItem => this.getCardMetricIdentifier(cardMetricItem)!);
      const cardMetricsToAdd = cardMetrics.filter(cardMetricItem => {
        const cardMetricIdentifier = this.getCardMetricIdentifier(cardMetricItem);
        if (cardMetricCollectionIdentifiers.includes(cardMetricIdentifier)) {
          return false;
        }
        cardMetricCollectionIdentifiers.push(cardMetricIdentifier);
        return true;
      });
      return [...cardMetricsToAdd, ...cardMetricCollection];
    }
    return cardMetricCollection;
  }
}
