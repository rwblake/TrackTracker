import dayjs from 'dayjs/esm';
import { CardType } from '../entities/enumerations/card-type.model';

// This interface combines all the relevant data for an account
export interface Account_Combined {
  id: number;
  spotifyID: string;
  avatarURL: string | null;
  bio: string | null;
  spotifyUsername: string;
  internalUser: InternalUser;
  userPreferencesID: number;
  feed: Feed;
  friends: Friendship[];
}

interface Feed {
  id: number;
  lastUpdated: dayjs.Dayjs;
  cards: Card[];
}

export interface Card {
  id: number;
  liked: boolean;
  metric?: CardType | null;
  timeFrame?: string | null;
  metricValue?: number | null;
  timeGenerated?: dayjs.Dayjs;
  belongsTo: {
    id: number;
    firstName: string;
    lastName: string;
  };
}

interface InternalUser {
  id: number;
  firstName: string;
  lastName: string;
  login: string;
  email: string;
}

interface Friendship {
  createdAt: dayjs.Dayjs;
  friendID: number;
  firstName: string;
  lastName: string;
  avatarURL: string;
}