import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StreamFormService, StreamFormGroup } from './stream-form.service';
import { IStream } from '../stream.model';
import { StreamService } from '../service/stream.service';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-stream-update',
  templateUrl: './stream-update.component.html',
})
export class StreamUpdateComponent implements OnInit {
  isSaving = false;
  stream: IStream | null = null;

  songsSharedCollection: ISong[] = [];
  appUsersSharedCollection: IAppUser[] = [];

  editForm: StreamFormGroup = this.streamFormService.createStreamFormGroup();

  constructor(
    protected streamService: StreamService,
    protected streamFormService: StreamFormService,
    protected songService: SongService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareSong = (o1: ISong | null, o2: ISong | null): boolean => this.songService.compareSong(o1, o2);

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stream }) => {
      this.stream = stream;
      if (stream) {
        this.updateForm(stream);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const stream = this.streamFormService.getStream(this.editForm);
    if (stream.id !== null) {
      this.subscribeToSaveResponse(this.streamService.update(stream));
    } else {
      this.subscribeToSaveResponse(this.streamService.create(stream));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStream>>): void {
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

  protected updateForm(stream: IStream): void {
    this.stream = stream;
    this.streamFormService.resetForm(this.editForm, stream);

    this.songsSharedCollection = this.songService.addSongToCollectionIfMissing<ISong>(this.songsSharedCollection, stream.song);
    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      stream.appUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.songService
      .query()
      .pipe(map((res: HttpResponse<ISong[]>) => res.body ?? []))
      .pipe(map((songs: ISong[]) => this.songService.addSongToCollectionIfMissing<ISong>(songs, this.stream?.song)))
      .subscribe((songs: ISong[]) => (this.songsSharedCollection = songs));

    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.stream?.appUser)))
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }
}
