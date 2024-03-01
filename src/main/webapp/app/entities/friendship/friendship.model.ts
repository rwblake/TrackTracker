import dayjs from 'dayjs/esm';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IFriendship {
  id: number;
  createdAt?: dayjs.Dayjs | null;
  friendInitiating?: Pick<IAppUser, 'id'> | null;
  friendAccepting?: Pick<IAppUser, 'id'> | null;
  appUser?: Pick<IAppUser, 'id'> | null;
}

export type NewFriendship = Omit<IFriendship, 'id'> & { id: null };
