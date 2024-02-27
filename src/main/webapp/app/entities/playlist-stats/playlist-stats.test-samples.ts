import dayjs from 'dayjs/esm';

import { IPlaylistStats, NewPlaylistStats } from './playlist-stats.model';

export const sampleWithRequiredData: IPlaylistStats = {
  id: 20474,
};

export const sampleWithPartialData: IPlaylistStats = {
  id: 96818,
  playlistLength: 4006,
};

export const sampleWithFullData: IPlaylistStats = {
  id: 64223,
  playlistLength: 41433,
  lastUpdated: dayjs('2024-02-27T21:39'),
};

export const sampleWithNewData: NewPlaylistStats = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
