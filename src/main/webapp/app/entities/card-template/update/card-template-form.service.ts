import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICardTemplate, NewCardTemplate } from '../card-template.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICardTemplate for edit and NewCardTemplateFormGroupInput for create.
 */
type CardTemplateFormGroupInput = ICardTemplate | PartialWithRequiredKeyOf<NewCardTemplate>;

type CardTemplateFormDefaults = Pick<NewCardTemplate, 'id'>;

type CardTemplateFormGroupContent = {
  id: FormControl<ICardTemplate['id'] | NewCardTemplate['id']>;
  color: FormControl<ICardTemplate['color']>;
  layout: FormControl<ICardTemplate['layout']>;
  name: FormControl<ICardTemplate['name']>;
  font: FormControl<ICardTemplate['font']>;
  appUser: FormControl<ICardTemplate['appUser']>;
};

export type CardTemplateFormGroup = FormGroup<CardTemplateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CardTemplateFormService {
  createCardTemplateFormGroup(cardTemplate: CardTemplateFormGroupInput = { id: null }): CardTemplateFormGroup {
    const cardTemplateRawValue = {
      ...this.getFormDefaults(),
      ...cardTemplate,
    };
    return new FormGroup<CardTemplateFormGroupContent>({
      id: new FormControl(
        { value: cardTemplateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      color: new FormControl(cardTemplateRawValue.color),
      layout: new FormControl(cardTemplateRawValue.layout),
      name: new FormControl(cardTemplateRawValue.name),
      font: new FormControl(cardTemplateRawValue.font),
      appUser: new FormControl(cardTemplateRawValue.appUser, {
        validators: [Validators.required],
      }),
    });
  }

  getCardTemplate(form: CardTemplateFormGroup): ICardTemplate | NewCardTemplate {
    return form.getRawValue() as ICardTemplate | NewCardTemplate;
  }

  resetForm(form: CardTemplateFormGroup, cardTemplate: CardTemplateFormGroupInput): void {
    const cardTemplateRawValue = { ...this.getFormDefaults(), ...cardTemplate };
    form.reset(
      {
        ...cardTemplateRawValue,
        id: { value: cardTemplateRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CardTemplateFormDefaults {
    return {
      id: null,
    };
  }
}
