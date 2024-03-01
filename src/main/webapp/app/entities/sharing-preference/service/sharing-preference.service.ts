import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISharingPreference, NewSharingPreference } from '../sharing-preference.model';

export type PartialUpdateSharingPreference = Partial<ISharingPreference> & Pick<ISharingPreference, 'id'>;

export type EntityResponseType = HttpResponse<ISharingPreference>;
export type EntityArrayResponseType = HttpResponse<ISharingPreference[]>;

@Injectable({ providedIn: 'root' })
export class SharingPreferenceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sharing-preferences');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(sharingPreference: NewSharingPreference): Observable<EntityResponseType> {
    return this.http.post<ISharingPreference>(this.resourceUrl, sharingPreference, { observe: 'response' });
  }

  update(sharingPreference: ISharingPreference): Observable<EntityResponseType> {
    return this.http.put<ISharingPreference>(
      `${this.resourceUrl}/${this.getSharingPreferenceIdentifier(sharingPreference)}`,
      sharingPreference,
      { observe: 'response' }
    );
  }

  partialUpdate(sharingPreference: PartialUpdateSharingPreference): Observable<EntityResponseType> {
    return this.http.patch<ISharingPreference>(
      `${this.resourceUrl}/${this.getSharingPreferenceIdentifier(sharingPreference)}`,
      sharingPreference,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISharingPreference>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISharingPreference[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSharingPreferenceIdentifier(sharingPreference: Pick<ISharingPreference, 'id'>): number {
    return sharingPreference.id;
  }

  compareSharingPreference(o1: Pick<ISharingPreference, 'id'> | null, o2: Pick<ISharingPreference, 'id'> | null): boolean {
    return o1 && o2 ? this.getSharingPreferenceIdentifier(o1) === this.getSharingPreferenceIdentifier(o2) : o1 === o2;
  }

  addSharingPreferenceToCollectionIfMissing<Type extends Pick<ISharingPreference, 'id'>>(
    sharingPreferenceCollection: Type[],
    ...sharingPreferencesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sharingPreferences: Type[] = sharingPreferencesToCheck.filter(isPresent);
    if (sharingPreferences.length > 0) {
      const sharingPreferenceCollectionIdentifiers = sharingPreferenceCollection.map(
        sharingPreferenceItem => this.getSharingPreferenceIdentifier(sharingPreferenceItem)!
      );
      const sharingPreferencesToAdd = sharingPreferences.filter(sharingPreferenceItem => {
        const sharingPreferenceIdentifier = this.getSharingPreferenceIdentifier(sharingPreferenceItem);
        if (sharingPreferenceCollectionIdentifiers.includes(sharingPreferenceIdentifier)) {
          return false;
        }
        sharingPreferenceCollectionIdentifiers.push(sharingPreferenceIdentifier);
        return true;
      });
      return [...sharingPreferencesToAdd, ...sharingPreferenceCollection];
    }
    return sharingPreferenceCollection;
  }
}
