import dayjs from 'dayjs/esm';

import { ISong, NewSong } from './song.model';

export const sampleWithRequiredData: ISong = {
  id: 99772,
  spotifyID: 'Health',
  name: 'magenta Dynamic',
  duration: '64493',
};

export const sampleWithPartialData: ISong = {
  id: 31009,
  spotifyID: 'Regional',
  name: 'Djibouti Plastic Underpass',
  imageURL: 'Tasty Shoals',
  duration: '13356',
  acousticness: 0,
  energy: 1,
  mode: false,
  timeSignature: 63316,
  valence: 0,
};

export const sampleWithFullData: ISong = {
  id: 8805,
  spotifyID: 'productize Dynamic copying',
  name: 'firewall panel static',
  imageURL: 'Andorra',
  releaseDate: dayjs('2024-02-26T17:32'),
  duration: '94515',
  acousticness: 0,
  danceability: 1,
  energy: 0,
  instrumentalness: 0,
  musicalKey: 34975,
  liveness: 0,
  loudness: 1675,
  mode: true,
  speechiness: 0,
  tempo: 15632,
  timeSignature: 49406,
  valence: 1,
};

export const sampleWithNewData: NewSong = {
  spotifyID: 'array vortals',
  name: 'Frozen Towels',
  duration: '24227',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
