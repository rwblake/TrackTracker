import { IArtist } from 'app/entities/artist/artist.model';

export interface IGenre {
  id: number;
  name?: string | null;
  artists?: Pick<IArtist, 'id'>[] | null;
}

export type NewGenre = Omit<IGenre, 'id'> & { id: null };
