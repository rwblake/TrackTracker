import { ISong } from 'app/entities/song/song.model';
import { IAlbum } from 'app/entities/album/album.model';
import { IGenre } from 'app/entities/genre/genre.model';

export interface IArtist {
  id: number;
  spotifyID?: string | null;
  name?: string | null;
  imageURL?: string | null;
  songs?: Pick<ISong, 'id' | 'name'>[] | null;
  albums?: Pick<IAlbum, 'id' | 'name'>[] | null;
  genres?: Pick<IGenre, 'id' | 'name'>[] | null;
}

export type NewArtist = Omit<IArtist, 'id'> & { id: null };
