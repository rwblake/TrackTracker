import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAppUser, NewAppUser } from '../app-user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppUser for edit and NewAppUserFormGroupInput for create.
 */
type AppUserFormGroupInput = IAppUser | PartialWithRequiredKeyOf<NewAppUser>;

type AppUserFormDefaults = Pick<NewAppUser, 'id'>;

type AppUserFormGroupContent = {
  id: FormControl<IAppUser['id'] | NewAppUser['id']>;
  spotifyID: FormControl<IAppUser['spotifyID']>;
  name: FormControl<IAppUser['name']>;
  avatarURL: FormControl<IAppUser['avatarURL']>;
  bio: FormControl<IAppUser['bio']>;
  spotifyUsername: FormControl<IAppUser['spotifyUsername']>;
  userPreferences: FormControl<IAppUser['userPreferences']>;
  spotifyToken: FormControl<IAppUser['spotifyToken']>;
  feed: FormControl<IAppUser['feed']>;
  blockedByUser: FormControl<IAppUser['blockedByUser']>;
};

export type AppUserFormGroup = FormGroup<AppUserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppUserFormService {
  createAppUserFormGroup(appUser: AppUserFormGroupInput = { id: null }): AppUserFormGroup {
    const appUserRawValue = {
      ...this.getFormDefaults(),
      ...appUser,
    };
    return new FormGroup<AppUserFormGroupContent>({
      id: new FormControl(
        { value: appUserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      spotifyID: new FormControl(appUserRawValue.spotifyID, {
        validators: [Validators.required],
      }),
      name: new FormControl(appUserRawValue.name, {
        validators: [Validators.required],
      }),
      avatarURL: new FormControl(appUserRawValue.avatarURL),
      bio: new FormControl(appUserRawValue.bio),
      spotifyUsername: new FormControl(appUserRawValue.spotifyUsername, {
        validators: [Validators.required],
      }),
      userPreferences: new FormControl(appUserRawValue.userPreferences, {
        validators: [Validators.required],
      }),
      spotifyToken: new FormControl(appUserRawValue.spotifyToken, {
        validators: [Validators.required],
      }),
      feed: new FormControl(appUserRawValue.feed, {
        validators: [Validators.required],
      }),
      blockedByUser: new FormControl(appUserRawValue.blockedByUser),
    });
  }

  getAppUser(form: AppUserFormGroup): IAppUser | NewAppUser {
    return form.getRawValue() as IAppUser | NewAppUser;
  }

  resetForm(form: AppUserFormGroup, appUser: AppUserFormGroupInput): void {
    const appUserRawValue = { ...this.getFormDefaults(), ...appUser };
    form.reset(
      {
        ...appUserRawValue,
        id: { value: appUserRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AppUserFormDefaults {
    return {
      id: null,
    };
  }
}
