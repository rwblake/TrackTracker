import dayjs from 'dayjs/esm';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IFriendRequest {
  id: number;
  createdAt?: dayjs.Dayjs | null;
  initiatingAppUser?: Pick<IAppUser, 'id'> | null;
  toAppUser?: Pick<IAppUser, 'id'> | null;
}

export type NewFriendRequest = Omit<IFriendRequest, 'id'> & { id: null };
