import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPlaylist, NewPlaylist } from '../playlist.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlaylist for edit and NewPlaylistFormGroupInput for create.
 */
type PlaylistFormGroupInput = IPlaylist | PartialWithRequiredKeyOf<NewPlaylist>;

type PlaylistFormDefaults = Pick<NewPlaylist, 'id' | 'songs'>;

type PlaylistFormGroupContent = {
  id: FormControl<IPlaylist['id'] | NewPlaylist['id']>;
  spotifyID: FormControl<IPlaylist['spotifyID']>;
  name: FormControl<IPlaylist['name']>;
  imageURL: FormControl<IPlaylist['imageURL']>;
  playlistStats: FormControl<IPlaylist['playlistStats']>;
  songs: FormControl<IPlaylist['songs']>;
  appUser: FormControl<IPlaylist['appUser']>;
};

export type PlaylistFormGroup = FormGroup<PlaylistFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlaylistFormService {
  createPlaylistFormGroup(playlist: PlaylistFormGroupInput = { id: null }): PlaylistFormGroup {
    const playlistRawValue = {
      ...this.getFormDefaults(),
      ...playlist,
    };
    return new FormGroup<PlaylistFormGroupContent>({
      id: new FormControl(
        { value: playlistRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      spotifyID: new FormControl(playlistRawValue.spotifyID, {
        validators: [Validators.required],
      }),
      name: new FormControl(playlistRawValue.name, {
        validators: [Validators.required],
      }),
      imageURL: new FormControl(playlistRawValue.imageURL),
      playlistStats: new FormControl(playlistRawValue.playlistStats, {
        validators: [Validators.required],
      }),
      songs: new FormControl(playlistRawValue.songs ?? []),
      appUser: new FormControl(playlistRawValue.appUser),
    });
  }

  getPlaylist(form: PlaylistFormGroup): IPlaylist | NewPlaylist {
    return form.getRawValue() as IPlaylist | NewPlaylist;
  }

  resetForm(form: PlaylistFormGroup, playlist: PlaylistFormGroupInput): void {
    const playlistRawValue = { ...this.getFormDefaults(), ...playlist };
    form.reset(
      {
        ...playlistRawValue,
        id: { value: playlistRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PlaylistFormDefaults {
    return {
      id: null,
      songs: [],
    };
  }
}
