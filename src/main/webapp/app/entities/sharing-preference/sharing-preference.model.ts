import { IUserPreferences } from 'app/entities/user-preferences/user-preferences.model';
import { CardType } from 'app/entities/enumerations/card-type.model';
import { VisibilityPreference } from 'app/entities/enumerations/visibility-preference.model';

export interface ISharingPreference {
  id: number;
  metric?: CardType | null;
  visibility?: VisibilityPreference | null;
  appUser?: Pick<IUserPreferences, 'id'> | null;
}

export type NewSharingPreference = Omit<ISharingPreference, 'id'> & { id: null };
