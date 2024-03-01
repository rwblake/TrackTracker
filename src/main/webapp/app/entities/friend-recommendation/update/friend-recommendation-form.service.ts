import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

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

type FriendRecommendationFormDefaults = Pick<NewFriendRecommendation, 'id'>;

type FriendRecommendationFormGroupContent = {
  id: FormControl<IFriendRecommendation['id'] | NewFriendRecommendation['id']>;
  similarity: FormControl<IFriendRecommendation['similarity']>;
  createdAt: FormControl<IFriendRecommendation['createdAt']>;
  aboutAppUser: FormControl<IFriendRecommendation['aboutAppUser']>;
  forAppUser: FormControl<IFriendRecommendation['forAppUser']>;
};

export type FriendRecommendationFormGroup = FormGroup<FriendRecommendationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FriendRecommendationFormService {
  createFriendRecommendationFormGroup(
    friendRecommendation: FriendRecommendationFormGroupInput = { id: null }
  ): FriendRecommendationFormGroup {
    const friendRecommendationRawValue = {
      ...this.getFormDefaults(),
      ...friendRecommendation,
    };
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
      forAppUser: new FormControl(friendRecommendationRawValue.forAppUser),
    });
  }

  getFriendRecommendation(form: FriendRecommendationFormGroup): IFriendRecommendation | NewFriendRecommendation {
    return form.getRawValue() as IFriendRecommendation | NewFriendRecommendation;
  }

  resetForm(form: FriendRecommendationFormGroup, friendRecommendation: FriendRecommendationFormGroupInput): void {
    const friendRecommendationRawValue = { ...this.getFormDefaults(), ...friendRecommendation };
    form.reset(
      {
        ...friendRecommendationRawValue,
        id: { value: friendRecommendationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FriendRecommendationFormDefaults {
    return {
      id: null,
    };
  }
}
