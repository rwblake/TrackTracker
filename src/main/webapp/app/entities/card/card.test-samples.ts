import dayjs from 'dayjs/esm';

import { CardType } from 'app/entities/enumerations/card-type.model';

import { ICard, NewCard } from './card.model';

export const sampleWithRequiredData: ICard = {
  id: 57013,
  metric: CardType['NO_OF_SONGS_LISTENED'],
  metricValue: 92281,
  timeGenerated: dayjs('2024-02-27T04:56'),
};

export const sampleWithPartialData: ICard = {
  id: 78922,
  metric: CardType['LISTENING_DURATION'],
  timeFrame: '10938',
  metricValue: 85712,
  timeGenerated: dayjs('2024-02-27T06:37'),
};

export const sampleWithFullData: ICard = {
  id: 24483,
  metric: CardType['FRIEND_REQUEST'],
  timeFrame: '74746',
  metricValue: 93918,
  timeGenerated: dayjs('2024-02-27T10:14'),
};

export const sampleWithNewData: NewCard = {
  metric: CardType['NO_OF_SONGS_LISTENED'],
  metricValue: 13933,
  timeGenerated: dayjs('2024-02-27T09:02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
