import dayjs from 'dayjs/esm';
import { IAlbum } from 'app/entities/album/album.model';
import { IPlaylist } from 'app/entities/playlist/playlist.model';
import { IArtist } from 'app/entities/artist/artist.model';

export interface ISong {
  id: number;
  spotifyID?: string | null;
  name?: string | null;
  imageURL?: string | null;
  releaseDate?: dayjs.Dayjs | null;
  duration?: string | null;
  acousticness?: number | null;
  danceability?: number | null;
  energy?: number | null;
  instrumentalness?: number | null;
  musicalKey?: number | null;
  liveness?: number | null;
  loudness?: number | null;
  mode?: boolean | null;
  speechiness?: number | null;
  tempo?: number | null;
  timeSignature?: number | null;
  valence?: number | null;
  album?: Pick<IAlbum, 'id'> | null;
  playlists?: Pick<IPlaylist, 'id'>[] | null;
  artists?: Pick<IArtist, 'id'>[] | null;
}

export type NewSong = Omit<ISong, 'id'> & { id: null };
