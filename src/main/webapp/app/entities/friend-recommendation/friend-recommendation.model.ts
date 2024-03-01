import dayjs from 'dayjs/esm';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IFriendRecommendation {
  id: number;
  similarity?: number | null;
  createdAt?: dayjs.Dayjs | null;
  aboutAppUser?: Pick<IAppUser, 'id'> | null;
  forAppUser?: Pick<IAppUser, 'id'> | null;
}

export type NewFriendRecommendation = Omit<IFriendRecommendation, 'id'> & { id: null };
