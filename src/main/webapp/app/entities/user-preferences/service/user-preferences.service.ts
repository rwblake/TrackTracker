import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserPreferences, NewUserPreferences } from '../user-preferences.model';

export type PartialUpdateUserPreferences = Partial<IUserPreferences> & Pick<IUserPreferences, 'id'>;

export type EntityResponseType = HttpResponse<IUserPreferences>;
export type EntityArrayResponseType = HttpResponse<IUserPreferences[]>;

@Injectable({ providedIn: 'root' })
export class UserPreferencesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-preferences');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userPreferences: NewUserPreferences): Observable<EntityResponseType> {
    return this.http.post<IUserPreferences>(this.resourceUrl, userPreferences, { observe: 'response' });
  }

  update(userPreferences: IUserPreferences): Observable<EntityResponseType> {
    return this.http.put<IUserPreferences>(`${this.resourceUrl}/${this.getUserPreferencesIdentifier(userPreferences)}`, userPreferences, {
      observe: 'response',
    });
  }

  partialUpdate(userPreferences: PartialUpdateUserPreferences): Observable<EntityResponseType> {
    return this.http.patch<IUserPreferences>(`${this.resourceUrl}/${this.getUserPreferencesIdentifier(userPreferences)}`, userPreferences, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserPreferences>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserPreferences[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserPreferencesIdentifier(userPreferences: Pick<IUserPreferences, 'id'>): number {
    return userPreferences.id;
  }

  compareUserPreferences(o1: Pick<IUserPreferences, 'id'> | null, o2: Pick<IUserPreferences, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserPreferencesIdentifier(o1) === this.getUserPreferencesIdentifier(o2) : o1 === o2;
  }

  addUserPreferencesToCollectionIfMissing<Type extends Pick<IUserPreferences, 'id'>>(
    userPreferencesCollection: Type[],
    ...userPreferencesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userPreferences: Type[] = userPreferencesToCheck.filter(isPresent);
    if (userPreferences.length > 0) {
      const userPreferencesCollectionIdentifiers = userPreferencesCollection.map(
        userPreferencesItem => this.getUserPreferencesIdentifier(userPreferencesItem)!
      );
      const userPreferencesToAdd = userPreferences.filter(userPreferencesItem => {
        const userPreferencesIdentifier = this.getUserPreferencesIdentifier(userPreferencesItem);
        if (userPreferencesCollectionIdentifiers.includes(userPreferencesIdentifier)) {
          return false;
        }
        userPreferencesCollectionIdentifiers.push(userPreferencesIdentifier);
        return true;
      });
      return [...userPreferencesToAdd, ...userPreferencesCollection];
    }
    return userPreferencesCollection;
  }
}
