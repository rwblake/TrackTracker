import dayjs from 'dayjs/esm';
import { IAppUser } from '../entities/app-user/app-user.model';

export interface IFriend {
  createdAt?: dayjs.Dayjs | null;
  initiatedByCurrentUser?: boolean;
  friendAppUser?: Pick<IAppUser, 'id' | 'internalUser' | 'avatarURL'>;
  id: number;
}
