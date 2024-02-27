import { VisibilityPreference } from 'app/entities/enumerations/visibility-preference.model';

export interface IUserPreferences {
  id: number;
  visibility?: VisibilityPreference | null;
  isDarkMode?: boolean | null;
  isHighContrast?: boolean | null;
}

export type NewUserPreferences = Omit<IUserPreferences, 'id'> & { id: null };
