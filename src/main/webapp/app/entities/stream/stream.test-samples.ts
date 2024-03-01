import dayjs from 'dayjs/esm';

import { IStream, NewStream } from './stream.model';

export const sampleWithRequiredData: IStream = {
  id: 12733,
  playedAt: dayjs('2024-02-26T19:42'),
};

export const sampleWithPartialData: IStream = {
  id: 29384,
  playedAt: dayjs('2024-02-26T15:42'),
};

export const sampleWithFullData: IStream = {
  id: 92978,
  playedAt: dayjs('2024-02-26T18:56'),
};

export const sampleWithNewData: NewStream = {
  playedAt: dayjs('2024-02-27T00:10'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
