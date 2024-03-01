import dayjs from 'dayjs/esm';

import { IFriendRequest, NewFriendRequest } from './friend-request.model';

export const sampleWithRequiredData: IFriendRequest = {
  id: 55423,
  createdAt: dayjs('2024-02-26T18:21'),
};

export const sampleWithPartialData: IFriendRequest = {
  id: 8754,
  createdAt: dayjs('2024-02-26T18:17'),
};

export const sampleWithFullData: IFriendRequest = {
  id: 29362,
  createdAt: dayjs('2024-02-27T05:12'),
};

export const sampleWithNewData: NewFriendRequest = {
  createdAt: dayjs('2024-02-27T05:13'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
