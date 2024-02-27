import dayjs from 'dayjs/esm';

import { IFriendship, NewFriendship } from './friendship.model';

export const sampleWithRequiredData: IFriendship = {
  id: 12823,
  createdAt: dayjs('2024-02-27T10:34'),
};

export const sampleWithPartialData: IFriendship = {
  id: 88722,
  createdAt: dayjs('2024-02-27T06:19'),
};

export const sampleWithFullData: IFriendship = {
  id: 54426,
  createdAt: dayjs('2024-02-27T06:06'),
};

export const sampleWithNewData: NewFriendship = {
  createdAt: dayjs('2024-02-27T20:55'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
