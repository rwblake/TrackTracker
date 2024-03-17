import dayjs from 'dayjs/esm';
import { IAppUser } from '../entities/app-user/app-user.model';
import { ISong } from '../entities/song/song.model';

export interface IFriend {
  createdAt?: dayjs.Dayjs | null;
  initiatedByCurrentUser?: boolean;
  friendAppUser?: Pick<IAppUser, 'id' | 'internalUser' | 'avatarURL'>;
  mostRecentSong?: ISong;
  id: number;
}
