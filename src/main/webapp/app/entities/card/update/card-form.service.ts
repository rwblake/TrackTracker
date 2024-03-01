import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICard, NewCard } from '../card.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICard for edit and NewCardFormGroupInput for create.
 */
type CardFormGroupInput = ICard | PartialWithRequiredKeyOf<NewCard>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICard | NewCard> = Omit<T, 'timeGenerated'> & {
  timeGenerated?: string | null;
};

type CardFormRawValue = FormValueOf<ICard>;

type NewCardFormRawValue = FormValueOf<NewCard>;

type CardFormDefaults = Pick<NewCard, 'id' | 'timeGenerated'>;

type CardFormGroupContent = {
  id: FormControl<CardFormRawValue['id'] | NewCard['id']>;
  metric: FormControl<CardFormRawValue['metric']>;
  timeFrame: FormControl<CardFormRawValue['timeFrame']>;
  metricValue: FormControl<CardFormRawValue['metricValue']>;
  timeGenerated: FormControl<CardFormRawValue['timeGenerated']>;
  appUser: FormControl<CardFormRawValue['appUser']>;
};

export type CardFormGroup = FormGroup<CardFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CardFormService {
  createCardFormGroup(card: CardFormGroupInput = { id: null }): CardFormGroup {
    const cardRawValue = this.convertCardToCardRawValue({
      ...this.getFormDefaults(),
      ...card,
    });
    return new FormGroup<CardFormGroupContent>({
      id: new FormControl(
        { value: cardRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      metric: new FormControl(cardRawValue.metric, {
        validators: [Validators.required],
      }),
      timeFrame: new FormControl(cardRawValue.timeFrame),
      metricValue: new FormControl(cardRawValue.metricValue, {
        validators: [Validators.required],
      }),
      timeGenerated: new FormControl(cardRawValue.timeGenerated, {
        validators: [Validators.required],
      }),
      appUser: new FormControl(cardRawValue.appUser, {
        validators: [Validators.required],
      }),
    });
  }

  getCard(form: CardFormGroup): ICard | NewCard {
    return this.convertCardRawValueToCard(form.getRawValue() as CardFormRawValue | NewCardFormRawValue);
  }

  resetForm(form: CardFormGroup, card: CardFormGroupInput): void {
    const cardRawValue = this.convertCardToCardRawValue({ ...this.getFormDefaults(), ...card });
    form.reset(
      {
        ...cardRawValue,
        id: { value: cardRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CardFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      timeGenerated: currentTime,
    };
  }

  private convertCardRawValueToCard(rawCard: CardFormRawValue | NewCardFormRawValue): ICard | NewCard {
    return {
      ...rawCard,
      timeGenerated: dayjs(rawCard.timeGenerated, DATE_TIME_FORMAT),
    };
  }

  private convertCardToCardRawValue(
    card: ICard | (Partial<NewCard> & CardFormDefaults)
  ): CardFormRawValue | PartialWithRequiredKeyOf<NewCardFormRawValue> {
    return {
      ...card,
      timeGenerated: card.timeGenerated ? card.timeGenerated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
