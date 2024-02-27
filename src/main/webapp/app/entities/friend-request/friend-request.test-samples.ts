import dayjs from 'dayjs/esm';

import { IFriendRequest, NewFriendRequest } from './friend-request.model';

export const sampleWithRequiredData: IFriendRequest = {
  id: 55423,
  createdAt: dayjs('2024-02-27T01:52'),
};

export const sampleWithPartialData: IFriendRequest = {
  id: 8754,
  createdAt: dayjs('2024-02-27T01:48'),
};

export const sampleWithFullData: IFriendRequest = {
  id: 29362,
  createdAt: dayjs('2024-02-27T12:43'),
};

export const sampleWithNewData: NewFriendRequest = {
  createdAt: dayjs('2024-02-27T12:43'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
