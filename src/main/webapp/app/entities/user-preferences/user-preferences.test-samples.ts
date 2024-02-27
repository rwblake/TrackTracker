import { VisibilityPreference } from 'app/entities/enumerations/visibility-preference.model';

import { IUserPreferences, NewUserPreferences } from './user-preferences.model';

export const sampleWithRequiredData: IUserPreferences = {
  id: 15863,
};

export const sampleWithPartialData: IUserPreferences = {
  id: 98844,
  visibility: VisibilityPreference['GLOBAL'],
  isDarkMode: true,
};

export const sampleWithFullData: IUserPreferences = {
  id: 95939,
  visibility: VisibilityPreference['GLOBAL'],
  isDarkMode: false,
  isHighContrast: false,
};

export const sampleWithNewData: NewUserPreferences = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
