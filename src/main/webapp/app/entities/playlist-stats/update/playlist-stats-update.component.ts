import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PlaylistStatsFormService, PlaylistStatsFormGroup } from './playlist-stats-form.service';
import { IPlaylistStats } from '../playlist-stats.model';
import { PlaylistStatsService } from '../service/playlist-stats.service';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';

@Component({
  selector: 'jhi-playlist-stats-update',
  templateUrl: './playlist-stats-update.component.html',
})
export class PlaylistStatsUpdateComponent implements OnInit {
  isSaving = false;
  playlistStats: IPlaylistStats | null = null;

  songsSharedCollection: ISong[] = [];

  editForm: PlaylistStatsFormGroup = this.playlistStatsFormService.createPlaylistStatsFormGroup();

  constructor(
    protected playlistStatsService: PlaylistStatsService,
    protected playlistStatsFormService: PlaylistStatsFormService,
    protected songService: SongService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareSong = (o1: ISong | null, o2: ISong | null): boolean => this.songService.compareSong(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playlistStats }) => {
      this.playlistStats = playlistStats;
      if (playlistStats) {
        this.updateForm(playlistStats);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const playlistStats = this.playlistStatsFormService.getPlaylistStats(this.editForm);
    if (playlistStats.id !== null) {
      this.subscribeToSaveResponse(this.playlistStatsService.update(playlistStats));
    } else {
      this.subscribeToSaveResponse(this.playlistStatsService.create(playlistStats));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlaylistStats>>): void {
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

  protected updateForm(playlistStats: IPlaylistStats): void {
    this.playlistStats = playlistStats;
    this.playlistStatsFormService.resetForm(this.editForm, playlistStats);

    this.songsSharedCollection = this.songService.addSongToCollectionIfMissing<ISong>(
      this.songsSharedCollection,
      playlistStats.happiestSong,
      playlistStats.fastestSong,
      playlistStats.sumsUpSong,
      playlistStats.anonmalousSong
    );
  }

  protected loadRelationshipsOptions(): void {
    this.songService
      .query()
      .pipe(map((res: HttpResponse<ISong[]>) => res.body ?? []))
      .pipe(
        map((songs: ISong[]) =>
          this.songService.addSongToCollectionIfMissing<ISong>(
            songs,
            this.playlistStats?.happiestSong,
            this.playlistStats?.fastestSong,
            this.playlistStats?.sumsUpSong,
            this.playlistStats?.anonmalousSong
          )
        )
      )
      .subscribe((songs: ISong[]) => (this.songsSharedCollection = songs));
  }
}
