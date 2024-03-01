import { VisibilityPreference } from 'app/entities/enumerations/visibility-preference.model';

import { IUserPreferences, NewUserPreferences } from './user-preferences.model';

export const sampleWithRequiredData: IUserPreferences = {
  id: 15863,
};

export const sampleWithPartialData: IUserPreferences = {
  id: 6717,
  visibility: VisibilityPreference['FRIENDS'],
  isDarkMode: true,
  playlistPrivacy: VisibilityPreference['GLOBAL'],
};

export const sampleWithFullData: IUserPreferences = {
  id: 11983,
  visibility: VisibilityPreference['FRIENDS_OF_FRIENDS'],
  isDarkMode: true,
  isHighContrast: false,
  playlistPrivacy: VisibilityPreference['PRIVATE'],
};

export const sampleWithNewData: NewUserPreferences = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
