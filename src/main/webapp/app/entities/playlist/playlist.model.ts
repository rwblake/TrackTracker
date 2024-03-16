import { IPlaylistStats } from 'app/entities/playlist-stats/playlist-stats.model';
import { ISong } from 'app/entities/song/song.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';

export interface IPlaylist {
  id: number;
  spotifyID?: string | null;
  name?: string | null;
  imageURL?: string | null;
  playlistStats?: Pick<IPlaylistStats, 'id'> | null;
  songs?: Pick<ISong, 'id'>[] | null;
  appUser?: Pick<IAppUser, 'id'> | null;
}

export type NewPlaylist = Omit<IPlaylist, 'id'> & { id: null };
