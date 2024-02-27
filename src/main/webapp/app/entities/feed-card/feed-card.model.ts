import { IFeed } from 'app/entities/feed/feed.model';
import { ICard } from 'app/entities/card/card.model';

export interface IFeedCard {
  id: number;
  liked?: boolean | null;
  feed?: Pick<IFeed, 'id'> | null;
  card?: Pick<ICard, 'id'> | null;
}

export type NewFeedCard = Omit<IFeedCard, 'id'> & { id: null };
