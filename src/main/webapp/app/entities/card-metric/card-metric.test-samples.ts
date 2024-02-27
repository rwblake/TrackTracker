import { CardType } from 'app/entities/enumerations/card-type.model';

import { ICardMetric, NewCardMetric } from './card-metric.model';

export const sampleWithRequiredData: ICardMetric = {
  id: 77659,
  metric: CardType['FRIEND_REQUEST'],
};

export const sampleWithPartialData: ICardMetric = {
  id: 69253,
  metric: CardType['FRIEND_REQUEST'],
};

export const sampleWithFullData: ICardMetric = {
  id: 46323,
  metric: CardType['FRIEND_REQUEST'],
};

export const sampleWithNewData: NewCardMetric = {
  metric: CardType['LISTENING_DURATION'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
