import { IUser } from 'app/entities/user/user.model';
import { IUserPreferences } from 'app/entities/user-preferences/user-preferences.model';
import { ISpotifyToken } from 'app/admin/spotify-token/spotify-token.model';
import { IFeed } from 'app/entities/feed/feed.model';

export interface IAppUser {
  id: number;
  spotifyID?: string | null;
  name?: string | null;
  avatarURL?: string | null;
  bio?: string | null;
  spotifyUsername?: string | null;
  internalUser?: Pick<IUser, 'id' | 'login' | 'firstName' | 'lastName'> | null;
  userPreferences?: Pick<IUserPreferences, 'id'> | null;
  spotifyToken?: Pick<ISpotifyToken, 'id'> | null;
  feed?: Pick<IFeed, 'id'> | null;
  blockedUsers?: Pick<IAppUser, 'id'>[] | null;
  blockedByUsers?: Pick<IAppUser, 'id'>[] | null;
}

export type NewAppUser = Omit<IAppUser, 'id'> & { id: null };
