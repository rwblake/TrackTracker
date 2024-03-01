import dayjs from 'dayjs/esm';

import { AlbumType } from 'app/entities/enumerations/album-type.model';

import { IAlbum, NewAlbum } from './album.model';

export const sampleWithRequiredData: IAlbum = {
  id: 51589,
  spotifyID: 'back-end',
  name: 'Chips',
};

export const sampleWithPartialData: IAlbum = {
  id: 18194,
  spotifyID: 'Markets virtual',
  name: 'loyalty Cambridgeshire Savings',
  imageURL: 'index',
};

export const sampleWithFullData: IAlbum = {
  id: 71559,
  spotifyID: 'Frozen Home',
  name: 'Account parse Music',
  imageURL: 'Licensed Mouse Computer',
  releaseDate: dayjs('2024-02-26T17:50'),
  albumType: AlbumType['COMPILATION'],
};

export const sampleWithNewData: NewAlbum = {
  spotifyID: 'turquoise action-items programming',
  name: 'withdrawal',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
