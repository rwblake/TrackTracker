import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFriendRequest, NewFriendRequest } from '../friend-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFriendRequest for edit and NewFriendRequestFormGroupInput for create.
 */
type FriendRequestFormGroupInput = IFriendRequest | PartialWithRequiredKeyOf<NewFriendRequest>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFriendRequest | NewFriendRequest> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type FriendRequestFormRawValue = FormValueOf<IFriendRequest>;

type NewFriendRequestFormRawValue = FormValueOf<NewFriendRequest>;

type FriendRequestFormDefaults = Pick<NewFriendRequest, 'id' | 'createdAt'>;

type FriendRequestFormGroupContent = {
  id: FormControl<FriendRequestFormRawValue['id'] | NewFriendRequest['id']>;
  createdAt: FormControl<FriendRequestFormRawValue['createdAt']>;
  initiatingAppUser: FormControl<FriendRequestFormRawValue['initiatingAppUser']>;
  toAppUser: FormControl<FriendRequestFormRawValue['toAppUser']>;
};

export type FriendRequestFormGroup = FormGroup<FriendRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FriendRequestFormService {
  createFriendRequestFormGroup(friendRequest: FriendRequestFormGroupInput = { id: null }): FriendRequestFormGroup {
    const friendRequestRawValue = this.convertFriendRequestToFriendRequestRawValue({
      ...this.getFormDefaults(),
      ...friendRequest,
    });
    return new FormGroup<FriendRequestFormGroupContent>({
      id: new FormControl(
        { value: friendRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      createdAt: new FormControl(friendRequestRawValue.createdAt, {
        validators: [Validators.required],
      }),
      initiatingAppUser: new FormControl(friendRequestRawValue.initiatingAppUser, {
        validators: [Validators.required],
      }),
      toAppUser: new FormControl(friendRequestRawValue.toAppUser),
    });
  }

  getFriendRequest(form: FriendRequestFormGroup): IFriendRequest | NewFriendRequest {
    return this.convertFriendRequestRawValueToFriendRequest(form.getRawValue() as FriendRequestFormRawValue | NewFriendRequestFormRawValue);
  }

  resetForm(form: FriendRequestFormGroup, friendRequest: FriendRequestFormGroupInput): void {
    const friendRequestRawValue = this.convertFriendRequestToFriendRequestRawValue({ ...this.getFormDefaults(), ...friendRequest });
    form.reset(
      {
        ...friendRequestRawValue,
        id: { value: friendRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FriendRequestFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertFriendRequestRawValueToFriendRequest(
    rawFriendRequest: FriendRequestFormRawValue | NewFriendRequestFormRawValue
  ): IFriendRequest | NewFriendRequest {
    return {
      ...rawFriendRequest,
      createdAt: dayjs(rawFriendRequest.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertFriendRequestToFriendRequestRawValue(
    friendRequest: IFriendRequest | (Partial<NewFriendRequest> & FriendRequestFormDefaults)
  ): FriendRequestFormRawValue | PartialWithRequiredKeyOf<NewFriendRequestFormRawValue> {
    return {
      ...friendRequest,
      createdAt: friendRequest.createdAt ? friendRequest.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
