import dayjs from 'dayjs/esm';

import { IFeed, NewFeed } from './feed.model';

export const sampleWithRequiredData: IFeed = {
  id: 86191,
  lastUpdated: dayjs('2024-02-27T14:04'),
};

export const sampleWithPartialData: IFeed = {
  id: 94488,
  lastUpdated: dayjs('2024-02-27T00:56'),
};

export const sampleWithFullData: IFeed = {
  id: 22783,
  lastUpdated: dayjs('2024-02-27T12:35'),
};

export const sampleWithNewData: NewFeed = {
  lastUpdated: dayjs('2024-02-27T10:04'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
