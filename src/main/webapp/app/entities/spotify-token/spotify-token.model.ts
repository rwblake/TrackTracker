import dayjs from 'dayjs/esm';

export interface ISpotifyToken {
  id: number;
  accessToken?: string | null;
  tokenType?: string | null;
  userScope?: string | null;
  expires?: dayjs.Dayjs | null;
  refreshToken?: string | null;
}

export type NewSpotifyToken = Omit<ISpotifyToken, 'id'> & { id: null };
