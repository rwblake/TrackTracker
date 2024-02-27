import dayjs from 'dayjs/esm';
import { IArtist } from 'app/entities/artist/artist.model';
import { AlbumType } from 'app/entities/enumerations/album-type.model';

export interface IAlbum {
  id: number;
  spotifyID?: string | null;
  name?: string | null;
  imageURL?: string | null;
  releaseDate?: dayjs.Dayjs | null;
  albumType?: AlbumType | null;
  artists?: Pick<IArtist, 'id'>[] | null;
}

export type NewAlbum = Omit<IAlbum, 'id'> & { id: null };
