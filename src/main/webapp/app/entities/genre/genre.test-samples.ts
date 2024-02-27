import { IGenre, NewGenre } from './genre.model';

export const sampleWithRequiredData: IGenre = {
  id: 52824,
};

export const sampleWithPartialData: IGenre = {
  id: 81462,
  name: 'Sleek Syrian Technician',
};

export const sampleWithFullData: IGenre = {
  id: 74579,
  name: 'Account Optimization Outdoors',
};

export const sampleWithNewData: NewGenre = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
