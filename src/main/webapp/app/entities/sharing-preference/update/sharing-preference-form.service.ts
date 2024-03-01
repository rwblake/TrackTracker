import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISharingPreference, NewSharingPreference } from '../sharing-preference.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISharingPreference for edit and NewSharingPreferenceFormGroupInput for create.
 */
type SharingPreferenceFormGroupInput = ISharingPreference | PartialWithRequiredKeyOf<NewSharingPreference>;

type SharingPreferenceFormDefaults = Pick<NewSharingPreference, 'id'>;

type SharingPreferenceFormGroupContent = {
  id: FormControl<ISharingPreference['id'] | NewSharingPreference['id']>;
  metric: FormControl<ISharingPreference['metric']>;
  visibility: FormControl<ISharingPreference['visibility']>;
  appUser: FormControl<ISharingPreference['appUser']>;
};

export type SharingPreferenceFormGroup = FormGroup<SharingPreferenceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SharingPreferenceFormService {
  createSharingPreferenceFormGroup(sharingPreference: SharingPreferenceFormGroupInput = { id: null }): SharingPreferenceFormGroup {
    const sharingPreferenceRawValue = {
      ...this.getFormDefaults(),
      ...sharingPreference,
    };
    return new FormGroup<SharingPreferenceFormGroupContent>({
      id: new FormControl(
        { value: sharingPreferenceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      metric: new FormControl(sharingPreferenceRawValue.metric),
      visibility: new FormControl(sharingPreferenceRawValue.visibility),
      appUser: new FormControl(sharingPreferenceRawValue.appUser, {
        validators: [Validators.required],
      }),
    });
  }

  getSharingPreference(form: SharingPreferenceFormGroup): ISharingPreference | NewSharingPreference {
    return form.getRawValue() as ISharingPreference | NewSharingPreference;
  }

  resetForm(form: SharingPreferenceFormGroup, sharingPreference: SharingPreferenceFormGroupInput): void {
    const sharingPreferenceRawValue = { ...this.getFormDefaults(), ...sharingPreference };
    form.reset(
      {
        ...sharingPreferenceRawValue,
        id: { value: sharingPreferenceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SharingPreferenceFormDefaults {
    return {
      id: null,
    };
  }
}
