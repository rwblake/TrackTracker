import { IAppUser } from 'app/entities/app-user/app-user.model';
import { Color } from 'app/entities/enumerations/color.model';
import { Layout } from 'app/entities/enumerations/layout.model';
import { Font } from 'app/entities/enumerations/font.model';

export interface ICardTemplate {
  id: number;
  color?: Color | null;
  layout?: Layout | null;
  name?: string | null;
  font?: Font | null;
  appUser?: Pick<IAppUser, 'id'> | null;
}

export type NewCardTemplate = Omit<ICardTemplate, 'id'> & { id: null };
