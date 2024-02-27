import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PlaylistFormService, PlaylistFormGroup } from './playlist-form.service';
import { IPlaylist } from '../playlist.model';
import { PlaylistService } from '../service/playlist.service';
import { IPlaylistStats } from 'app/entities/playlist-stats/playlist-stats.model';
import { PlaylistStatsService } from 'app/entities/playlist-stats/service/playlist-stats.service';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-playlist-update',
  templateUrl: './playlist-update.component.html',
})
export class PlaylistUpdateComponent implements OnInit {
  isSaving = false;
  playlist: IPlaylist | null = null;

  playlistStatsCollection: IPlaylistStats[] = [];
  songsSharedCollection: ISong[] = [];
  appUsersSharedCollection: IAppUser[] = [];

  editForm: PlaylistFormGroup = this.playlistFormService.createPlaylistFormGroup();

  constructor(
    protected playlistService: PlaylistService,
    protected playlistFormService: PlaylistFormService,
    protected playlistStatsService: PlaylistStatsService,
    protected songService: SongService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePlaylistStats = (o1: IPlaylistStats | null, o2: IPlaylistStats | null): boolean =>
    this.playlistStatsService.comparePlaylistStats(o1, o2);

  compareSong = (o1: ISong | null, o2: ISong | null): boolean => this.songService.compareSong(o1, o2);

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playlist }) => {
      this.playlist = playlist;
      if (playlist) {
        this.updateForm(playlist);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const playlist = this.playlistFormService.getPlaylist(this.editForm);
    if (playlist.id !== null) {
      this.subscribeToSaveResponse(this.playlistService.update(playlist));
    } else {
      this.subscribeToSaveResponse(this.playlistService.create(playlist));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlaylist>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(playlist: IPlaylist): void {
    this.playlist = playlist;
    this.playlistFormService.resetForm(this.editForm, playlist);

    this.playlistStatsCollection = this.playlistStatsService.addPlaylistStatsToCollectionIfMissing<IPlaylistStats>(
      this.playlistStatsCollection,
      playlist.playlistStats
    );
    this.songsSharedCollection = this.songService.addSongToCollectionIfMissing<ISong>(
      this.songsSharedCollection,
      ...(playlist.songs ?? [])
    );
    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      playlist.appUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.playlistStatsService
      .query({ filter: 'playlist-is-null' })
      .pipe(map((res: HttpResponse<IPlaylistStats[]>) => res.body ?? []))
      .pipe(
        map((playlistStats: IPlaylistStats[]) =>
          this.playlistStatsService.addPlaylistStatsToCollectionIfMissing<IPlaylistStats>(playlistStats, this.playlist?.playlistStats)
        )
      )
      .subscribe((playlistStats: IPlaylistStats[]) => (this.playlistStatsCollection = playlistStats));

    this.songService
      .query()
      .pipe(map((res: HttpResponse<ISong[]>) => res.body ?? []))
      .pipe(map((songs: ISong[]) => this.songService.addSongToCollectionIfMissing<ISong>(songs, ...(this.playlist?.songs ?? []))))
      .subscribe((songs: ISong[]) => (this.songsSharedCollection = songs));

    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.playlist?.appUser)))
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }
}
