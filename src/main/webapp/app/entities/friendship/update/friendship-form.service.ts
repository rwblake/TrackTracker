import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFriendship, NewFriendship } from '../friendship.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFriendship for edit and NewFriendshipFormGroupInput for create.
 */
type FriendshipFormGroupInput = IFriendship | PartialWithRequiredKeyOf<NewFriendship>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFriendship | NewFriendship> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type FriendshipFormRawValue = FormValueOf<IFriendship>;

type NewFriendshipFormRawValue = FormValueOf<NewFriendship>;

type FriendshipFormDefaults = Pick<NewFriendship, 'id' | 'createdAt'>;

type FriendshipFormGroupContent = {
  id: FormControl<FriendshipFormRawValue['id'] | NewFriendship['id']>;
  createdAt: FormControl<FriendshipFormRawValue['createdAt']>;
  friendInitiating: FormControl<FriendshipFormRawValue['friendInitiating']>;
  friendAccepting: FormControl<FriendshipFormRawValue['friendAccepting']>;
};

export type FriendshipFormGroup = FormGroup<FriendshipFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FriendshipFormService {
  createFriendshipFormGroup(friendship: FriendshipFormGroupInput = { id: null }): FriendshipFormGroup {
    const friendshipRawValue = this.convertFriendshipToFriendshipRawValue({
      ...this.getFormDefaults(),
      ...friendship,
    });
    return new FormGroup<FriendshipFormGroupContent>({
      id: new FormControl(
        { value: friendshipRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      createdAt: new FormControl(friendshipRawValue.createdAt, {
        validators: [Validators.required],
      }),
      friendInitiating: new FormControl(friendshipRawValue.friendInitiating, {
        validators: [Validators.required],
      }),
      friendAccepting: new FormControl(friendshipRawValue.friendAccepting, {
        validators: [Validators.required],
      }),
    });
  }

  getFriendship(form: FriendshipFormGroup): IFriendship | NewFriendship {
    return this.convertFriendshipRawValueToFriendship(form.getRawValue() as FriendshipFormRawValue | NewFriendshipFormRawValue);
  }

  resetForm(form: FriendshipFormGroup, friendship: FriendshipFormGroupInput): void {
    const friendshipRawValue = this.convertFriendshipToFriendshipRawValue({ ...this.getFormDefaults(), ...friendship });
    form.reset(
      {
        ...friendshipRawValue,
        id: { value: friendshipRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FriendshipFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertFriendshipRawValueToFriendship(
    rawFriendship: FriendshipFormRawValue | NewFriendshipFormRawValue
  ): IFriendship | NewFriendship {
    return {
      ...rawFriendship,
      createdAt: dayjs(rawFriendship.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertFriendshipToFriendshipRawValue(
    friendship: IFriendship | (Partial<NewFriendship> & FriendshipFormDefaults)
  ): FriendshipFormRawValue | PartialWithRequiredKeyOf<NewFriendshipFormRawValue> {
    return {
      ...friendship,
      createdAt: friendship.createdAt ? friendship.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
