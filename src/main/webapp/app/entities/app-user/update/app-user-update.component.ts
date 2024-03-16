import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AppUserFormService, AppUserFormGroup } from './app-user-form.service';
import { IAppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IUserPreferences } from 'app/entities/user-preferences/user-preferences.model';
import { UserPreferencesService } from 'app/entities/user-preferences/service/user-preferences.service';
import { ISpotifyToken } from 'app/entities/spotify-token/spotify-token.model';
import { SpotifyTokenService } from 'app/entities/spotify-token/service/spotify-token.service';
import { IFeed } from 'app/entities/feed/feed.model';
import { FeedService } from 'app/entities/feed/service/feed.service';

@Component({
  selector: 'jhi-app-user-update',
  templateUrl: './app-user-update.component.html',
})
export class AppUserUpdateComponent implements OnInit {
  isSaving = false;
  appUser: IAppUser | null = null;

  usersSharedCollection: IUser[] = [];
  userPreferencesCollection: IUserPreferences[] = [];
  spotifyTokensCollection: ISpotifyToken[] = [];
  feedsCollection: IFeed[] = [];
  appUsersSharedCollection: IAppUser[] = [];

  editForm: AppUserFormGroup = this.appUserFormService.createAppUserFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected appUserService: AppUserService,
    protected appUserFormService: AppUserFormService,
    protected userService: UserService,
    protected userPreferencesService: UserPreferencesService,
    protected spotifyTokenService: SpotifyTokenService,
    protected feedService: FeedService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareUserPreferences = (o1: IUserPreferences | null, o2: IUserPreferences | null): boolean =>
    this.userPreferencesService.compareUserPreferences(o1, o2);

  compareSpotifyToken = (o1: ISpotifyToken | null, o2: ISpotifyToken | null): boolean =>
    this.spotifyTokenService.compareSpotifyToken(o1, o2);

  compareFeed = (o1: IFeed | null, o2: IFeed | null): boolean => this.feedService.compareFeed(o1, o2);

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appUser }) => {
      this.appUser = appUser;
      if (appUser) {
        this.updateForm(appUser);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('teamprojectApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appUser = this.appUserFormService.getAppUser(this.editForm);
    if (appUser.id !== null) {
      this.subscribeToSaveResponse(this.appUserService.update(appUser));
    } else {
      this.subscribeToSaveResponse(this.appUserService.create(appUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppUser>>): void {
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

  protected updateForm(appUser: IAppUser): void {
    this.appUser = appUser;
    this.appUserFormService.resetForm(this.editForm, appUser);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, appUser.internalUser);
    this.userPreferencesCollection = this.userPreferencesService.addUserPreferencesToCollectionIfMissing<IUserPreferences>(
      this.userPreferencesCollection,
      appUser.userPreferences
    );
    this.spotifyTokensCollection = this.spotifyTokenService.addSpotifyTokenToCollectionIfMissing<ISpotifyToken>(
      this.spotifyTokensCollection,
      appUser.spotifyToken
    );
    this.feedsCollection = this.feedService.addFeedToCollectionIfMissing<IFeed>(this.feedsCollection, appUser.feed);
    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      ...(appUser.blockedUsers ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.appUser?.internalUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.userPreferencesService
      .query({ filter: 'appuser-is-null' })
      .pipe(map((res: HttpResponse<IUserPreferences[]>) => res.body ?? []))
      .pipe(
        map((userPreferences: IUserPreferences[]) =>
          this.userPreferencesService.addUserPreferencesToCollectionIfMissing<IUserPreferences>(
            userPreferences,
            this.appUser?.userPreferences
          )
        )
      )
      .subscribe((userPreferences: IUserPreferences[]) => (this.userPreferencesCollection = userPreferences));

    this.spotifyTokenService
      .query({ filter: 'appuser-is-null' })
      .pipe(map((res: HttpResponse<ISpotifyToken[]>) => res.body ?? []))
      .pipe(
        map((spotifyTokens: ISpotifyToken[]) =>
          this.spotifyTokenService.addSpotifyTokenToCollectionIfMissing<ISpotifyToken>(spotifyTokens, this.appUser?.spotifyToken)
        )
      )
      .subscribe((spotifyTokens: ISpotifyToken[]) => (this.spotifyTokensCollection = spotifyTokens));

    this.feedService
      .query({ filter: 'appuser-is-null' })
      .pipe(map((res: HttpResponse<IFeed[]>) => res.body ?? []))
      .pipe(map((feeds: IFeed[]) => this.feedService.addFeedToCollectionIfMissing<IFeed>(feeds, this.appUser?.feed)))
      .subscribe((feeds: IFeed[]) => (this.feedsCollection = feeds));

    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, ...(this.appUser?.blockedUsers ?? []))
        )
      )
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }
}
