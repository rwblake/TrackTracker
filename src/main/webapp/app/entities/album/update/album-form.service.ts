import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAlbum, NewAlbum } from '../album.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAlbum for edit and NewAlbumFormGroupInput for create.
 */
type AlbumFormGroupInput = IAlbum | PartialWithRequiredKeyOf<NewAlbum>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAlbum | NewAlbum> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

type AlbumFormRawValue = FormValueOf<IAlbum>;

type NewAlbumFormRawValue = FormValueOf<NewAlbum>;

type AlbumFormDefaults = Pick<NewAlbum, 'id' | 'releaseDate' | 'artists'>;

type AlbumFormGroupContent = {
  id: FormControl<AlbumFormRawValue['id'] | NewAlbum['id']>;
  spotifyID: FormControl<AlbumFormRawValue['spotifyID']>;
  name: FormControl<AlbumFormRawValue['name']>;
  imageURL: FormControl<AlbumFormRawValue['imageURL']>;
  releaseDate: FormControl<AlbumFormRawValue['releaseDate']>;
  albumType: FormControl<AlbumFormRawValue['albumType']>;
  artists: FormControl<AlbumFormRawValue['artists']>;
};

export type AlbumFormGroup = FormGroup<AlbumFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AlbumFormService {
  createAlbumFormGroup(album: AlbumFormGroupInput = { id: null }): AlbumFormGroup {
    const albumRawValue = this.convertAlbumToAlbumRawValue({
      ...this.getFormDefaults(),
      ...album,
    });
    return new FormGroup<AlbumFormGroupContent>({
      id: new FormControl(
        { value: albumRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      spotifyID: new FormControl(albumRawValue.spotifyID, {
        validators: [Validators.required],
      }),
      name: new FormControl(albumRawValue.name, {
        validators: [Validators.required],
      }),
      imageURL: new FormControl(albumRawValue.imageURL),
      releaseDate: new FormControl(albumRawValue.releaseDate),
      albumType: new FormControl(albumRawValue.albumType),
      artists: new FormControl(albumRawValue.artists ?? []),
    });
  }

  getAlbum(form: AlbumFormGroup): IAlbum | NewAlbum {
    return this.convertAlbumRawValueToAlbum(form.getRawValue() as AlbumFormRawValue | NewAlbumFormRawValue);
  }

  resetForm(form: AlbumFormGroup, album: AlbumFormGroupInput): void {
    const albumRawValue = this.convertAlbumToAlbumRawValue({ ...this.getFormDefaults(), ...album });
    form.reset(
      {
        ...albumRawValue,
        id: { value: albumRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AlbumFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      releaseDate: currentTime,
      artists: [],
    };
  }

  private convertAlbumRawValueToAlbum(rawAlbum: AlbumFormRawValue | NewAlbumFormRawValue): IAlbum | NewAlbum {
    return {
      ...rawAlbum,
      releaseDate: dayjs(rawAlbum.releaseDate, DATE_TIME_FORMAT),
    };
  }

  private convertAlbumToAlbumRawValue(
    album: IAlbum | (Partial<NewAlbum> & AlbumFormDefaults)
  ): AlbumFormRawValue | PartialWithRequiredKeyOf<NewAlbumFormRawValue> {
    return {
      ...album,
      releaseDate: album.releaseDate ? album.releaseDate.format(DATE_TIME_FORMAT) : undefined,
      artists: album.artists ?? [],
    };
  }
}
