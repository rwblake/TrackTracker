import { CardType } from 'app/entities/enumerations/card-type.model';
import { VisibilityPreference } from 'app/entities/enumerations/visibility-preference.model';

import { ISharingPreference, NewSharingPreference } from './sharing-preference.model';

export const sampleWithRequiredData: ISharingPreference = {
  id: 43095,
};

export const sampleWithPartialData: ISharingPreference = {
  id: 7120,
};

export const sampleWithFullData: ISharingPreference = {
  id: 78797,
  metric: CardType['LISTENING_DURATION'],
  visibility: VisibilityPreference['FRIENDS'],
};

export const sampleWithNewData: NewSharingPreference = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
