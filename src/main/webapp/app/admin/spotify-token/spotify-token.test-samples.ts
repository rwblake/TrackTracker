import dayjs from 'dayjs/esm';

import { ISpotifyToken, NewSpotifyToken } from './spotify-token.model';

export const sampleWithRequiredData: ISpotifyToken = {
  id: 95673,
  accessToken: 'Plaza portal',
  tokenType: 'Avon',
  userScope: 'ivory',
  expires: dayjs('2024-02-27T15:10'),
  refreshToken: 'Iceland Grocery invoice',
};

export const sampleWithPartialData: ISpotifyToken = {
  id: 75472,
  accessToken: 'seize',
  tokenType: 'primary',
  userScope: 'Dakota',
  expires: dayjs('2024-02-27T00:48'),
  refreshToken: 'Pine array Concrete',
};

export const sampleWithFullData: ISpotifyToken = {
  id: 61688,
  accessToken: 'feed protocol',
  tokenType: 'system-worthy',
  userScope: 'portal Facilitator',
  expires: dayjs('2024-02-26T22:02'),
  refreshToken: 'Shilling Lithuania Investment',
};

export const sampleWithNewData: NewSpotifyToken = {
  accessToken: 'Coordinator',
  tokenType: 'Montenegro maroon experiences',
  userScope: 'Sports Beauty',
  expires: dayjs('2024-02-27T07:59'),
  refreshToken: 'black',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
