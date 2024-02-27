import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICardTemplate, NewCardTemplate } from '../card-template.model';

export type PartialUpdateCardTemplate = Partial<ICardTemplate> & Pick<ICardTemplate, 'id'>;

export type EntityResponseType = HttpResponse<ICardTemplate>;
export type EntityArrayResponseType = HttpResponse<ICardTemplate[]>;

@Injectable({ providedIn: 'root' })
export class CardTemplateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/card-templates');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cardTemplate: NewCardTemplate): Observable<EntityResponseType> {
    return this.http.post<ICardTemplate>(this.resourceUrl, cardTemplate, { observe: 'response' });
  }

  update(cardTemplate: ICardTemplate): Observable<EntityResponseType> {
    return this.http.put<ICardTemplate>(`${this.resourceUrl}/${this.getCardTemplateIdentifier(cardTemplate)}`, cardTemplate, {
      observe: 'response',
    });
  }

  partialUpdate(cardTemplate: PartialUpdateCardTemplate): Observable<EntityResponseType> {
    return this.http.patch<ICardTemplate>(`${this.resourceUrl}/${this.getCardTemplateIdentifier(cardTemplate)}`, cardTemplate, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICardTemplate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICardTemplate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCardTemplateIdentifier(cardTemplate: Pick<ICardTemplate, 'id'>): number {
    return cardTemplate.id;
  }

  compareCardTemplate(o1: Pick<ICardTemplate, 'id'> | null, o2: Pick<ICardTemplate, 'id'> | null): boolean {
    return o1 && o2 ? this.getCardTemplateIdentifier(o1) === this.getCardTemplateIdentifier(o2) : o1 === o2;
  }

  addCardTemplateToCollectionIfMissing<Type extends Pick<ICardTemplate, 'id'>>(
    cardTemplateCollection: Type[],
    ...cardTemplatesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cardTemplates: Type[] = cardTemplatesToCheck.filter(isPresent);
    if (cardTemplates.length > 0) {
      const cardTemplateCollectionIdentifiers = cardTemplateCollection.map(
        cardTemplateItem => this.getCardTemplateIdentifier(cardTemplateItem)!
      );
      const cardTemplatesToAdd = cardTemplates.filter(cardTemplateItem => {
        const cardTemplateIdentifier = this.getCardTemplateIdentifier(cardTemplateItem);
        if (cardTemplateCollectionIdentifiers.includes(cardTemplateIdentifier)) {
          return false;
        }
        cardTemplateCollectionIdentifiers.push(cardTemplateIdentifier);
        return true;
      });
      return [...cardTemplatesToAdd, ...cardTemplateCollection];
    }
    return cardTemplateCollection;
  }
}
