import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGenre, NewGenre } from '../genre.model';

export type PartialUpdateGenre = Partial<IGenre> & Pick<IGenre, 'id'>;

export type EntityResponseType = HttpResponse<IGenre>;
export type EntityArrayResponseType = HttpResponse<IGenre[]>;

@Injectable({ providedIn: 'root' })
export class GenreService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/genres');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(genre: NewGenre): Observable<EntityResponseType> {
    return this.http.post<IGenre>(this.resourceUrl, genre, { observe: 'response' });
  }

  update(genre: IGenre): Observable<EntityResponseType> {
    return this.http.put<IGenre>(`${this.resourceUrl}/${this.getGenreIdentifier(genre)}`, genre, { observe: 'response' });
  }

  partialUpdate(genre: PartialUpdateGenre): Observable<EntityResponseType> {
    return this.http.patch<IGenre>(`${this.resourceUrl}/${this.getGenreIdentifier(genre)}`, genre, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGenre>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGenre[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGenreIdentifier(genre: Pick<IGenre, 'id'>): number {
    return genre.id;
  }

  compareGenre(o1: Pick<IGenre, 'id'> | null, o2: Pick<IGenre, 'id'> | null): boolean {
    return o1 && o2 ? this.getGenreIdentifier(o1) === this.getGenreIdentifier(o2) : o1 === o2;
  }

  addGenreToCollectionIfMissing<Type extends Pick<IGenre, 'id'>>(
    genreCollection: Type[],
    ...genresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const genres: Type[] = genresToCheck.filter(isPresent);
    if (genres.length > 0) {
      const genreCollectionIdentifiers = genreCollection.map(genreItem => this.getGenreIdentifier(genreItem)!);
      const genresToAdd = genres.filter(genreItem => {
        const genreIdentifier = this.getGenreIdentifier(genreItem);
        if (genreCollectionIdentifiers.includes(genreIdentifier)) {
          return false;
        }
        genreCollectionIdentifiers.push(genreIdentifier);
        return true;
      });
      return [...genresToAdd, ...genreCollection];
    }
    return genreCollection;
  }
}
