import dayjs from 'dayjs/esm';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { CardType } from 'app/entities/enumerations/card-type.model';

export interface ICard {
  id: number;
  metric?: CardType | null;
  timeFrame?: string | null;
  metricValue?: number | null;
  timeGenerated?: dayjs.Dayjs | null;
  appUser?: Pick<IAppUser, 'id'> | null;
}

export type NewCard = Omit<ICard, 'id'> & { id: null };
