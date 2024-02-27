import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStream, NewStream } from '../stream.model';

export type PartialUpdateStream = Partial<IStream> & Pick<IStream, 'id'>;

type RestOf<T extends IStream | NewStream> = Omit<T, 'playedAt'> & {
  playedAt?: string | null;
};

export type RestStream = RestOf<IStream>;

export type NewRestStream = RestOf<NewStream>;

export type PartialUpdateRestStream = RestOf<PartialUpdateStream>;

export type EntityResponseType = HttpResponse<IStream>;
export type EntityArrayResponseType = HttpResponse<IStream[]>;

@Injectable({ providedIn: 'root' })
export class StreamService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/streams');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(stream: NewStream): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stream);
    return this.http
      .post<RestStream>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(stream: IStream): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stream);
    return this.http
      .put<RestStream>(`${this.resourceUrl}/${this.getStreamIdentifier(stream)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(stream: PartialUpdateStream): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stream);
    return this.http
      .patch<RestStream>(`${this.resourceUrl}/${this.getStreamIdentifier(stream)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStream>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStream[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStreamIdentifier(stream: Pick<IStream, 'id'>): number {
    return stream.id;
  }

  compareStream(o1: Pick<IStream, 'id'> | null, o2: Pick<IStream, 'id'> | null): boolean {
    return o1 && o2 ? this.getStreamIdentifier(o1) === this.getStreamIdentifier(o2) : o1 === o2;
  }

  addStreamToCollectionIfMissing<Type extends Pick<IStream, 'id'>>(
    streamCollection: Type[],
    ...streamsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const streams: Type[] = streamsToCheck.filter(isPresent);
    if (streams.length > 0) {
      const streamCollectionIdentifiers = streamCollection.map(streamItem => this.getStreamIdentifier(streamItem)!);
      const streamsToAdd = streams.filter(streamItem => {
        const streamIdentifier = this.getStreamIdentifier(streamItem);
        if (streamCollectionIdentifiers.includes(streamIdentifier)) {
          return false;
        }
        streamCollectionIdentifiers.push(streamIdentifier);
        return true;
      });
      return [...streamsToAdd, ...streamCollection];
    }
    return streamCollection;
  }

  protected convertDateFromClient<T extends IStream | NewStream | PartialUpdateStream>(stream: T): RestOf<T> {
    return {
      ...stream,
      playedAt: stream.playedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restStream: RestStream): IStream {
    return {
      ...restStream,
      playedAt: restStream.playedAt ? dayjs(restStream.playedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStream>): HttpResponse<IStream> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStream[]>): HttpResponse<IStream[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
