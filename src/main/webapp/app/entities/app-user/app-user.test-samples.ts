import { IAppUser, NewAppUser } from './app-user.model';

export const sampleWithRequiredData: IAppUser = {
  id: 1883,
  spotifyID: 'compressing Latvian Account',
  name: 'Outdoors Health Gloves',
  spotifyUsername: 'Nepal',
};

export const sampleWithPartialData: IAppUser = {
  id: 3203,
  spotifyID: 'Sleek',
  name: 'Mountains',
  avatarURL: 'monitor',
  spotifyUsername: 'Checking solid',
};

export const sampleWithFullData: IAppUser = {
  id: 55911,
  spotifyID: 'applications',
  name: 'infomediaries',
  avatarURL: 'panel',
  bio: '../fake-data/blob/hipster.txt',
  spotifyUsername: 'Harbor Card Summit',
};

export const sampleWithNewData: NewAppUser = {
  spotifyID: 'black',
  name: 'Electronics Profit-focused Total',
  spotifyUsername: 'grey Planner green',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
