import { IFeedCard, NewFeedCard } from './feed-card.model';

export const sampleWithRequiredData: IFeedCard = {
  id: 8493,
  liked: false,
};

export const sampleWithPartialData: IFeedCard = {
  id: 79133,
  liked: false,
};

export const sampleWithFullData: IFeedCard = {
  id: 39443,
  liked: false,
};

export const sampleWithNewData: NewFeedCard = {
  liked: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
