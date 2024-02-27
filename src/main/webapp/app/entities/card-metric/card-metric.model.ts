import { ICardTemplate } from 'app/entities/card-template/card-template.model';
import { CardType } from 'app/entities/enumerations/card-type.model';

export interface ICardMetric {
  id: number;
  metric?: CardType | null;
  cardTemplate?: Pick<ICardTemplate, 'id'> | null;
}

export type NewCardMetric = Omit<ICardMetric, 'id'> & { id: null };
