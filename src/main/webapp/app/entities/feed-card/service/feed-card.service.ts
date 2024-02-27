import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFeedCard, NewFeedCard } from '../feed-card.model';

export type PartialUpdateFeedCard = Partial<IFeedCard> & Pick<IFeedCard, 'id'>;

export type EntityResponseType = HttpResponse<IFeedCard>;
export type EntityArrayResponseType = HttpResponse<IFeedCard[]>;

@Injectable({ providedIn: 'root' })
export class FeedCardService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/feed-cards');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(feedCard: NewFeedCard): Observable<EntityResponseType> {
    return this.http.post<IFeedCard>(this.resourceUrl, feedCard, { observe: 'response' });
  }

  update(feedCard: IFeedCard): Observable<EntityResponseType> {
    return this.http.put<IFeedCard>(`${this.resourceUrl}/${this.getFeedCardIdentifier(feedCard)}`, feedCard, { observe: 'response' });
  }

  partialUpdate(feedCard: PartialUpdateFeedCard): Observable<EntityResponseType> {
    return this.http.patch<IFeedCard>(`${this.resourceUrl}/${this.getFeedCardIdentifier(feedCard)}`, feedCard, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFeedCard>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFeedCard[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFeedCardIdentifier(feedCard: Pick<IFeedCard, 'id'>): number {
    return feedCard.id;
  }

  compareFeedCard(o1: Pick<IFeedCard, 'id'> | null, o2: Pick<IFeedCard, 'id'> | null): boolean {
    return o1 && o2 ? this.getFeedCardIdentifier(o1) === this.getFeedCardIdentifier(o2) : o1 === o2;
  }

  addFeedCardToCollectionIfMissing<Type extends Pick<IFeedCard, 'id'>>(
    feedCardCollection: Type[],
    ...feedCardsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const feedCards: Type[] = feedCardsToCheck.filter(isPresent);
    if (feedCards.length > 0) {
      const feedCardCollectionIdentifiers = feedCardCollection.map(feedCardItem => this.getFeedCardIdentifier(feedCardItem)!);
      const feedCardsToAdd = feedCards.filter(feedCardItem => {
        const feedCardIdentifier = this.getFeedCardIdentifier(feedCardItem);
        if (feedCardCollectionIdentifiers.includes(feedCardIdentifier)) {
          return false;
        }
        feedCardCollectionIdentifiers.push(feedCardIdentifier);
        return true;
      });
      return [...feedCardsToAdd, ...feedCardCollection];
    }
    return feedCardCollection;
  }
}
