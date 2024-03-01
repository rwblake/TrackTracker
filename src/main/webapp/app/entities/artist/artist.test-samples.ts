import { IArtist, NewArtist } from './artist.model';

export const sampleWithRequiredData: IArtist = {
  id: 70921,
  spotifyID: 'synthesizing Kentucky Park',
  name: 'Sweden incubate',
};

export const sampleWithPartialData: IArtist = {
  id: 81685,
  spotifyID: 'Kids Mali Incredible',
  name: 'Steel',
};

export const sampleWithFullData: IArtist = {
  id: 41541,
  spotifyID: 'Dynamic engage',
  name: 'up Fantastic paradigm',
  imageURL: 'port Strategist Games',
};

export const sampleWithNewData: NewArtist = {
  spotifyID: 'calculate',
  name: 'programming envisioneer',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
