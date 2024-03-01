import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICardMetric, NewCardMetric } from '../card-metric.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICardMetric for edit and NewCardMetricFormGroupInput for create.
 */
type CardMetricFormGroupInput = ICardMetric | PartialWithRequiredKeyOf<NewCardMetric>;

type CardMetricFormDefaults = Pick<NewCardMetric, 'id'>;

type CardMetricFormGroupContent = {
  id: FormControl<ICardMetric['id'] | NewCardMetric['id']>;
  metric: FormControl<ICardMetric['metric']>;
  cardTemplate: FormControl<ICardMetric['cardTemplate']>;
};

export type CardMetricFormGroup = FormGroup<CardMetricFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CardMetricFormService {
  createCardMetricFormGroup(cardMetric: CardMetricFormGroupInput = { id: null }): CardMetricFormGroup {
    const cardMetricRawValue = {
      ...this.getFormDefaults(),
      ...cardMetric,
    };
    return new FormGroup<CardMetricFormGroupContent>({
      id: new FormControl(
        { value: cardMetricRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      metric: new FormControl(cardMetricRawValue.metric, {
        validators: [Validators.required],
      }),
      cardTemplate: new FormControl(cardMetricRawValue.cardTemplate, {
        validators: [Validators.required],
      }),
    });
  }

  getCardMetric(form: CardMetricFormGroup): ICardMetric | NewCardMetric {
    return form.getRawValue() as ICardMetric | NewCardMetric;
  }

  resetForm(form: CardMetricFormGroup, cardMetric: CardMetricFormGroupInput): void {
    const cardMetricRawValue = { ...this.getFormDefaults(), ...cardMetric };
    form.reset(
      {
        ...cardMetricRawValue,
        id: { value: cardMetricRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CardMetricFormDefaults {
    return {
      id: null,
    };
  }
}
