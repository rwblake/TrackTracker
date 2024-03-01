import { IPlaylist, NewPlaylist } from './playlist.model';

export const sampleWithRequiredData: IPlaylist = {
  id: 45404,
  spotifyID: 'dynamic',
  name: 'New Loan',
};

export const sampleWithPartialData: IPlaylist = {
  id: 59037,
  spotifyID: 'Malta Chief',
  name: 'hardware',
  imageURL: 'Plastic Open-architected FTP',
};

export const sampleWithFullData: IPlaylist = {
  id: 34360,
  spotifyID: 'virtual Account',
  name: 'online',
  imageURL: 'Investment',
};

export const sampleWithNewData: NewPlaylist = {
  spotifyID: 'next-generation Fresh transmitter',
  name: 'revolutionary Directives',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
