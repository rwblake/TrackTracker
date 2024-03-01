import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISong, NewSong } from '../song.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISong for edit and NewSongFormGroupInput for create.
 */
type SongFormGroupInput = ISong | PartialWithRequiredKeyOf<NewSong>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISong | NewSong> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

type SongFormRawValue = FormValueOf<ISong>;

type NewSongFormRawValue = FormValueOf<NewSong>;

type SongFormDefaults = Pick<NewSong, 'id' | 'releaseDate' | 'mode' | 'playlists' | 'artists'>;

type SongFormGroupContent = {
  id: FormControl<SongFormRawValue['id'] | NewSong['id']>;
  spotifyID: FormControl<SongFormRawValue['spotifyID']>;
  name: FormControl<SongFormRawValue['name']>;
  imageURL: FormControl<SongFormRawValue['imageURL']>;
  releaseDate: FormControl<SongFormRawValue['releaseDate']>;
  duration: FormControl<SongFormRawValue['duration']>;
  acousticness: FormControl<SongFormRawValue['acousticness']>;
  danceability: FormControl<SongFormRawValue['danceability']>;
  energy: FormControl<SongFormRawValue['energy']>;
  instrumentalness: FormControl<SongFormRawValue['instrumentalness']>;
  musicalKey: FormControl<SongFormRawValue['musicalKey']>;
  liveness: FormControl<SongFormRawValue['liveness']>;
  loudness: FormControl<SongFormRawValue['loudness']>;
  mode: FormControl<SongFormRawValue['mode']>;
  speechiness: FormControl<SongFormRawValue['speechiness']>;
  tempo: FormControl<SongFormRawValue['tempo']>;
  timeSignature: FormControl<SongFormRawValue['timeSignature']>;
  valence: FormControl<SongFormRawValue['valence']>;
  album: FormControl<SongFormRawValue['album']>;
  playlists: FormControl<SongFormRawValue['playlists']>;
  artists: FormControl<SongFormRawValue['artists']>;
};

export type SongFormGroup = FormGroup<SongFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SongFormService {
  createSongFormGroup(song: SongFormGroupInput = { id: null }): SongFormGroup {
    const songRawValue = this.convertSongToSongRawValue({
      ...this.getFormDefaults(),
      ...song,
    });
    return new FormGroup<SongFormGroupContent>({
      id: new FormControl(
        { value: songRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      spotifyID: new FormControl(songRawValue.spotifyID, {
        validators: [Validators.required],
      }),
      name: new FormControl(songRawValue.name, {
        validators: [Validators.required],
      }),
      imageURL: new FormControl(songRawValue.imageURL),
      releaseDate: new FormControl(songRawValue.releaseDate),
      duration: new FormControl(songRawValue.duration, {
        validators: [Validators.required],
      }),
      acousticness: new FormControl(songRawValue.acousticness, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      danceability: new FormControl(songRawValue.danceability, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      energy: new FormControl(songRawValue.energy, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      instrumentalness: new FormControl(songRawValue.instrumentalness, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      musicalKey: new FormControl(songRawValue.musicalKey),
      liveness: new FormControl(songRawValue.liveness, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      loudness: new FormControl(songRawValue.loudness, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      mode: new FormControl(songRawValue.mode),
      speechiness: new FormControl(songRawValue.speechiness, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      tempo: new FormControl(songRawValue.tempo),
      timeSignature: new FormControl(songRawValue.timeSignature),
      valence: new FormControl(songRawValue.valence, {
        validators: [Validators.min(0.0), Validators.max(1.0)],
      }),
      album: new FormControl(songRawValue.album),
      playlists: new FormControl(songRawValue.playlists ?? []),
      artists: new FormControl(songRawValue.artists ?? []),
    });
  }

  getSong(form: SongFormGroup): ISong | NewSong {
    return this.convertSongRawValueToSong(form.getRawValue() as SongFormRawValue | NewSongFormRawValue);
  }

  resetForm(form: SongFormGroup, song: SongFormGroupInput): void {
    const songRawValue = this.convertSongToSongRawValue({ ...this.getFormDefaults(), ...song });
    form.reset(
      {
        ...songRawValue,
        id: { value: songRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SongFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      releaseDate: currentTime,
      mode: false,
      playlists: [],
      artists: [],
    };
  }

  private convertSongRawValueToSong(rawSong: SongFormRawValue | NewSongFormRawValue): ISong | NewSong {
    return {
      ...rawSong,
      releaseDate: dayjs(rawSong.releaseDate, DATE_TIME_FORMAT),
    };
  }

  private convertSongToSongRawValue(
    song: ISong | (Partial<NewSong> & SongFormDefaults)
  ): SongFormRawValue | PartialWithRequiredKeyOf<NewSongFormRawValue> {
    return {
      ...song,
      releaseDate: song.releaseDate ? song.releaseDate.format(DATE_TIME_FORMAT) : undefined,
      playlists: song.playlists ?? [],
      artists: song.artists ?? [],
    };
  }
}
