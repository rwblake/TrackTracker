import dayjs from 'dayjs/esm';

import { IFriendship, NewFriendship } from './friendship.model';

export const sampleWithRequiredData: IFriendship = {
  id: 12823,
  createdAt: dayjs('2024-02-27T03:03'),
};

export const sampleWithPartialData: IFriendship = {
  id: 88722,
  createdAt: dayjs('2024-02-26T22:48'),
};

export const sampleWithFullData: IFriendship = {
  id: 54426,
  createdAt: dayjs('2024-02-26T22:35'),
};

export const sampleWithNewData: NewFriendship = {
  createdAt: dayjs('2024-02-27T13:24'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
