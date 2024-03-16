import dayjs from 'dayjs/esm';

import { IFriendRecommendation, NewFriendRecommendation } from './friend-recommendation.model';

export const sampleWithRequiredData: IFriendRecommendation = {
  id: 51845,
  similarity: 0,
  createdAt: dayjs('2024-02-26T20:38'),
};

export const sampleWithPartialData: IFriendRecommendation = {
  id: 14063,
  similarity: 1,
  createdAt: dayjs('2024-02-27T14:06'),
};

export const sampleWithFullData: IFriendRecommendation = {
  id: 12958,
  similarity: 1,
  createdAt: dayjs('2024-02-26T16:57'),
};

export const sampleWithNewData: NewFriendRecommendation = {
  similarity: 1,
  createdAt: dayjs('2024-02-27T12:10'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
