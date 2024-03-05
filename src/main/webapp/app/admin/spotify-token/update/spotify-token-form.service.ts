import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISpotifyToken, NewSpotifyToken } from '../spotify-token.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISpotifyToken for edit and NewSpotifyTokenFormGroupInput for create.
 */
type SpotifyTokenFormGroupInput = ISpotifyToken | PartialWithRequiredKeyOf<NewSpotifyToken>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISpotifyToken | NewSpotifyToken> = Omit<T, 'expires'> & {
  expires?: string | null;
};

type SpotifyTokenFormRawValue = FormValueOf<ISpotifyToken>;

type NewSpotifyTokenFormRawValue = FormValueOf<NewSpotifyToken>;

type SpotifyTokenFormDefaults = Pick<NewSpotifyToken, 'id' | 'expires'>;

type SpotifyTokenFormGroupContent = {
  id: FormControl<SpotifyTokenFormRawValue['id'] | NewSpotifyToken['id']>;
  accessToken: FormControl<SpotifyTokenFormRawValue['accessToken']>;
  tokenType: FormControl<SpotifyTokenFormRawValue['tokenType']>;
  userScope: FormControl<SpotifyTokenFormRawValue['userScope']>;
  expires: FormControl<SpotifyTokenFormRawValue['expires']>;
  refreshToken: FormControl<SpotifyTokenFormRawValue['refreshToken']>;
};

export type SpotifyTokenFormGroup = FormGroup<SpotifyTokenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SpotifyTokenFormService {
  createSpotifyTokenFormGroup(spotifyToken: SpotifyTokenFormGroupInput = { id: null }): SpotifyTokenFormGroup {
    const spotifyTokenRawValue = this.convertSpotifyTokenToSpotifyTokenRawValue({
      ...this.getFormDefaults(),
      ...spotifyToken,
    });
    return new FormGroup<SpotifyTokenFormGroupContent>({
      id: new FormControl(
        { value: spotifyTokenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      accessToken: new FormControl(spotifyTokenRawValue.accessToken, {
        validators: [Validators.required],
      }),
      tokenType: new FormControl(spotifyTokenRawValue.tokenType, {
        validators: [Validators.required],
      }),
      userScope: new FormControl(spotifyTokenRawValue.userScope, {
        validators: [Validators.required],
      }),
      expires: new FormControl(spotifyTokenRawValue.expires, {
        validators: [Validators.required],
      }),
      refreshToken: new FormControl(spotifyTokenRawValue.refreshToken, {
        validators: [Validators.required],
      }),
    });
  }

  getSpotifyToken(form: SpotifyTokenFormGroup): ISpotifyToken | NewSpotifyToken {
    return this.convertSpotifyTokenRawValueToSpotifyToken(form.getRawValue() as SpotifyTokenFormRawValue | NewSpotifyTokenFormRawValue);
  }

  resetForm(form: SpotifyTokenFormGroup, spotifyToken: SpotifyTokenFormGroupInput): void {
    const spotifyTokenRawValue = this.convertSpotifyTokenToSpotifyTokenRawValue({ ...this.getFormDefaults(), ...spotifyToken });
    form.reset(
      {
        ...spotifyTokenRawValue,
        id: { value: spotifyTokenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SpotifyTokenFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      expires: currentTime,
    };
  }

  private convertSpotifyTokenRawValueToSpotifyToken(
    rawSpotifyToken: SpotifyTokenFormRawValue | NewSpotifyTokenFormRawValue
  ): ISpotifyToken | NewSpotifyToken {
    return {
      ...rawSpotifyToken,
      expires: dayjs(rawSpotifyToken.expires, DATE_TIME_FORMAT),
    };
  }

  private convertSpotifyTokenToSpotifyTokenRawValue(
    spotifyToken: ISpotifyToken | (Partial<NewSpotifyToken> & SpotifyTokenFormDefaults)
  ): SpotifyTokenFormRawValue | PartialWithRequiredKeyOf<NewSpotifyTokenFormRawValue> {
    return {
      ...spotifyToken,
      expires: spotifyToken.expires ? spotifyToken.expires.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
