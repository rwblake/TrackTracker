import { Color } from 'app/entities/enumerations/color.model';
import { Layout } from 'app/entities/enumerations/layout.model';
import { Font } from 'app/entities/enumerations/font.model';

import { ICardTemplate, NewCardTemplate } from './card-template.model';

export const sampleWithRequiredData: ICardTemplate = {
  id: 42350,
};

export const sampleWithPartialData: ICardTemplate = {
  id: 51601,
  name: 'digital Jewelery customized',
};

export const sampleWithFullData: ICardTemplate = {
  id: 71767,
  color: Color['OPTION1'],
  layout: Layout['OPTION1'],
  name: 'Rand Fish',
  font: Font['OPTION1'],
};

export const sampleWithNewData: NewCardTemplate = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
