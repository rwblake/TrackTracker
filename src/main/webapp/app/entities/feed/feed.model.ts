import dayjs from 'dayjs/esm';

export interface IFeed {
  id: number;
  lastUpdated?: dayjs.Dayjs | null;
}

export type NewFeed = Omit<IFeed, 'id'> & { id: null };
