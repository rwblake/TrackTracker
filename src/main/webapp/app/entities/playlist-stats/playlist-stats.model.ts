import dayjs from 'dayjs/esm';
import { ISong } from 'app/entities/song/song.model';

export interface IPlaylistStats {
  id: number;
  playlistLength?: number | null;
  lastUpdated?: dayjs.Dayjs | null;
  happiestSong?: Pick<ISong, 'id'> | null;
  fastestSong?: Pick<ISong, 'id'> | null;
  sumsUpSong?: Pick<ISong, 'id'> | null;
  anonmalousSong?: Pick<ISong, 'id'> | null;
}

export type NewPlaylistStats = Omit<IPlaylistStats, 'id'> & { id: null };
