import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPlaylistStats, NewPlaylistStats } from '../playlist-stats.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlaylistStats for edit and NewPlaylistStatsFormGroupInput for create.
 */
type PlaylistStatsFormGroupInput = IPlaylistStats | PartialWithRequiredKeyOf<NewPlaylistStats>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPlaylistStats | NewPlaylistStats> = Omit<T, 'lastUpdated'> & {
  lastUpdated?: string | null;
};

type PlaylistStatsFormRawValue = FormValueOf<IPlaylistStats>;

type NewPlaylistStatsFormRawValue = FormValueOf<NewPlaylistStats>;

type PlaylistStatsFormDefaults = Pick<NewPlaylistStats, 'id' | 'lastUpdated'>;

type PlaylistStatsFormGroupContent = {
  id: FormControl<PlaylistStatsFormRawValue['id'] | NewPlaylistStats['id']>;
  playlistLength: FormControl<PlaylistStatsFormRawValue['playlistLength']>;
  lastUpdated: FormControl<PlaylistStatsFormRawValue['lastUpdated']>;
  happiestSong: FormControl<PlaylistStatsFormRawValue['happiestSong']>;
  fastestSong: FormControl<PlaylistStatsFormRawValue['fastestSong']>;
  sumsUpSong: FormControl<PlaylistStatsFormRawValue['sumsUpSong']>;
  anonmalousSong: FormControl<PlaylistStatsFormRawValue['anonmalousSong']>;
};

export type PlaylistStatsFormGroup = FormGroup<PlaylistStatsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlaylistStatsFormService {
  createPlaylistStatsFormGroup(playlistStats: PlaylistStatsFormGroupInput = { id: null }): PlaylistStatsFormGroup {
    const playlistStatsRawValue = this.convertPlaylistStatsToPlaylistStatsRawValue({
      ...this.getFormDefaults(),
      ...playlistStats,
    });
    return new FormGroup<PlaylistStatsFormGroupContent>({
      id: new FormControl(
        { value: playlistStatsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      playlistLength: new FormControl(playlistStatsRawValue.playlistLength),
      lastUpdated: new FormControl(playlistStatsRawValue.lastUpdated),
      happiestSong: new FormControl(playlistStatsRawValue.happiestSong),
      fastestSong: new FormControl(playlistStatsRawValue.fastestSong),
      sumsUpSong: new FormControl(playlistStatsRawValue.sumsUpSong),
      anonmalousSong: new FormControl(playlistStatsRawValue.anonmalousSong),
    });
  }

  getPlaylistStats(form: PlaylistStatsFormGroup): IPlaylistStats | NewPlaylistStats {
    return this.convertPlaylistStatsRawValueToPlaylistStats(form.getRawValue() as PlaylistStatsFormRawValue | NewPlaylistStatsFormRawValue);
  }

  resetForm(form: PlaylistStatsFormGroup, playlistStats: PlaylistStatsFormGroupInput): void {
    const playlistStatsRawValue = this.convertPlaylistStatsToPlaylistStatsRawValue({ ...this.getFormDefaults(), ...playlistStats });
    form.reset(
      {
        ...playlistStatsRawValue,
        id: { value: playlistStatsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PlaylistStatsFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastUpdated: currentTime,
    };
  }

  private convertPlaylistStatsRawValueToPlaylistStats(
    rawPlaylistStats: PlaylistStatsFormRawValue | NewPlaylistStatsFormRawValue
  ): IPlaylistStats | NewPlaylistStats {
    return {
      ...rawPlaylistStats,
      lastUpdated: dayjs(rawPlaylistStats.lastUpdated, DATE_TIME_FORMAT),
    };
  }

  private convertPlaylistStatsToPlaylistStatsRawValue(
    playlistStats: IPlaylistStats | (Partial<NewPlaylistStats> & PlaylistStatsFormDefaults)
  ): PlaylistStatsFormRawValue | PartialWithRequiredKeyOf<NewPlaylistStatsFormRawValue> {
    return {
      ...playlistStats,
      lastUpdated: playlistStats.lastUpdated ? playlistStats.lastUpdated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
