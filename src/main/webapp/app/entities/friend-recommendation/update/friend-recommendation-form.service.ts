import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFriendRecommendation, NewFriendRecommendation } from '../friend-recommendation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFriendRecommendation for edit and NewFriendRecommendationFormGroupInput for create.
 */
type FriendRecommendationFormGroupInput = IFriendRecommendation | PartialWithRequiredKeyOf<NewFriendRecommendation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFriendRecommendation | NewFriendRecommendation> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type FriendRecommendationFormRawValue = FormValueOf<IFriendRecommendation>;

type NewFriendRecommendationFormRawValue = FormValueOf<NewFriendRecommendation>;

type FriendRecommendationFormDefaults = Pick<NewFriendRecommendation, 'id' | 'createdAt'>;

type FriendRecommendationFormGroupContent = {
  id: FormControl<FriendRecommendationFormRawValue['id'] | NewFriendRecommendation['id']>;
  similarity: FormControl<FriendRecommendationFormRawValue['similarity']>;
  createdAt: FormControl<FriendRecommendationFormRawValue['createdAt']>;
  aboutAppUser: FormControl<FriendRecommendationFormRawValue['aboutAppUser']>;
  forAppUser: FormControl<FriendRecommendationFormRawValue['forAppUser']>;
};

export type FriendRecommendationFormGroup = FormGroup<FriendRecommendationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FriendRecommendationFormService {
  createFriendRecommendationFormGroup(
    friendRecommendation: FriendRecommendationFormGroupInput = { id: null }
  ): FriendRecommendationFormGroup {
    const friendRecommendationRawValue = this.convertFriendRecommendationToFriendRecommendationRawValue({
      ...this.getFormDefaults(),
      ...friendRecommendation,
    });
    return new FormGroup<FriendRecommendationFormGroupContent>({
      id: new FormControl(
        { value: friendRecommendationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      similarity: new FormControl(friendRecommendationRawValue.similarity, {
        validators: [Validators.required, Validators.min(0.0), Validators.max(1.0)],
      }),
      createdAt: new FormControl(friendRecommendationRawValue.createdAt, {
        validators: [Validators.required],
      }),
      aboutAppUser: new FormControl(friendRecommendationRawValue.aboutAppUser, {
        validators: [Validators.required],
      }),
      forAppUser: new FormControl(friendRecommendationRawValue.forAppUser, {
        validators: [Validators.required],
      }),
    });
  }

  getFriendRecommendation(form: FriendRecommendationFormGroup): IFriendRecommendation | NewFriendRecommendation {
    return this.convertFriendRecommendationRawValueToFriendRecommendation(
      form.getRawValue() as FriendRecommendationFormRawValue | NewFriendRecommendationFormRawValue
    );
  }

  resetForm(form: FriendRecommendationFormGroup, friendRecommendation: FriendRecommendationFormGroupInput): void {
    const friendRecommendationRawValue = this.convertFriendRecommendationToFriendRecommendationRawValue({
      ...this.getFormDefaults(),
      ...friendRecommendation,
    });
    form.reset(
      {
        ...friendRecommendationRawValue,
        id: { value: friendRecommendationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FriendRecommendationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertFriendRecommendationRawValueToFriendRecommendation(
    rawFriendRecommendation: FriendRecommendationFormRawValue | NewFriendRecommendationFormRawValue
  ): IFriendRecommendation | NewFriendRecommendation {
    return {
      ...rawFriendRecommendation,
      createdAt: dayjs(rawFriendRecommendation.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertFriendRecommendationToFriendRecommendationRawValue(
    friendRecommendation: IFriendRecommendation | (Partial<NewFriendRecommendation> & FriendRecommendationFormDefaults)
  ): FriendRecommendationFormRawValue | PartialWithRequiredKeyOf<NewFriendRecommendationFormRawValue> {
    return {
      ...friendRecommendation,
      createdAt: friendRecommendation.createdAt ? friendRecommendation.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
