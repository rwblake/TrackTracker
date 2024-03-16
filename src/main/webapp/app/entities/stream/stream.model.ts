import dayjs from 'dayjs/esm';
import { ISong } from 'app/entities/song/song.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IStream {
  id: number;
  playedAt?: dayjs.Dayjs | null;
  song?: Pick<ISong, 'id'> | null;
  appUser?: Pick<IAppUser, 'id'> | null;
}

export type NewStream = Omit<IStream, 'id'> & { id: null };
