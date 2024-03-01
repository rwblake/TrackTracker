import dayjs from 'dayjs/esm';

import { IFeed, NewFeed } from './feed.model';

export const sampleWithRequiredData: IFeed = {
  id: 86191,
  lastUpdated: dayjs('2024-02-27T06:34'),
};

export const sampleWithPartialData: IFeed = {
  id: 94488,
  lastUpdated: dayjs('2024-02-26T17:26'),
};

export const sampleWithFullData: IFeed = {
  id: 22783,
  lastUpdated: dayjs('2024-02-27T05:04'),
};

export const sampleWithNewData: NewFeed = {
  lastUpdated: dayjs('2024-02-27T02:34'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
