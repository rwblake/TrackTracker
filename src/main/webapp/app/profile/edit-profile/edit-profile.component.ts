import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { finalize, takeUntil } from 'rxjs/operators';
import { AccountService } from '../../core/auth/account.service';
import { Account } from '../../core/auth/account.model';
import { Observable, Subject } from 'rxjs';
import { IAppUser } from '../../entities/app-user/app-user.model';
import { IUser } from '../../entities/user/user.model';
import { IUserPreferences } from '../../entities/user-preferences/user-preferences.model';
import { ISpotifyToken } from '../../admin/spotify-token/spotify-token.model';
import { IFeed } from '../../entities/feed/feed.model';
import { AppUserService } from '../../entities/app-user/service/app-user.service';
import { HttpResponse } from '@angular/common/http';
import { VisibilityPreference } from '../../entities/enumerations/visibility-preference.model';

@Component({
  selector: 'jhi-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss'],
})
export class EditProfileComponent implements OnInit {
  private readonly destroy$ = new Subject<void>();
  isSaving = false;
  account: Account | null = null;
  user: IAppUser | null = null;
  //background: string = 'black';
  username: string | undefined;
  pic: string | null | undefined;
  bio: string | null | undefined;
  profilePriv: number | undefined;
  profilePriv2: VisibilityPreference | undefined;
  playlistPriv: number | undefined;
  playlistPriv2: VisibilityPreference | undefined;
  darkMode: boolean | undefined;
  highContrast: boolean | undefined;

  preferencesForm: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    pic: new FormControl('', [Validators.required]),
    bio: new FormControl('', [Validators.required]),
    profilePriv: new FormControl('', [Validators.required]),
    playlistPriv: new FormControl('', [Validators.required]),
    darkMode: new FormControl('', [Validators.required]),
    highContrast: new FormControl('', [Validators.required]),
  });

  constructor(protected appUserService: AppUserService, private accountService: AccountService) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
    this.accountService.fetchUser().subscribe(
      (data: IAppUser) => {
        this.user = data;
      },
      error => {
        console.error('Error fetching user bio:', error);
      }
    );
  }
  onSubmit(): void {
    this.username = this.preferencesForm.get('username')?.value;
    if (this.username == '') {
      this.username = this.account?.login;
    }
    this.pic = this.preferencesForm.get('pic')?.value;
    if (this.pic == '') {
      this.pic = this.user?.avatarURL;
    }
    this.bio = this.preferencesForm.get('bio')?.value;
    if (this.bio == '') {
      this.bio = this.user?.bio;
    }
    this.profilePriv = this.preferencesForm.get('profilePriv')?.value;
    if (this.profilePriv == 0) {
      this.profilePriv = 0;
    }
    this.playlistPriv = this.preferencesForm.get('playlistPriv')?.value;
    if (this.playlistPriv == 0) {
      this.playlistPriv = 0;
    }
    this.darkMode = this.preferencesForm.get('darkMode')?.value;
    // if (this.darkMode == false) {
    //   this.background = 'white';
    // }
    if (this.darkMode == false) {
      this.darkMode = false;
    }
    this.highContrast = this.preferencesForm.get('highContrast')?.value;
    if (this.highContrast == false) {
      this.highContrast = false;
    }

    const myPreferences: IUserPreferences = {
      // @ts-ignore
      id: this.user?.userPreferences?.id,
      visibility: this.profilePriv2,
      isDarkMode: this.darkMode,
      isHighContrast: this.highContrast,
      playlistPrivacy: this.playlistPriv2,
    };

    const myAppUser: IAppUser = {
      // @ts-ignore
      id: this.user?.id,
      spotifyID: this.user?.spotifyID,
      name: this.user?.name,
      avatarURL: this.pic,
      bio: this.bio,
      spotifyUsername: this.user?.spotifyUsername,
      internalUser: this.user?.internalUser,
      userPreferences: this.user?.userPreferences,
      spotifyToken: this.user?.spotifyToken,
      feed: this.user?.feed,
      blockedByUser: this.user?.blockedByUser,
    };

    this.isSaving = true;
    this.subscribeToSaveResponse(this.appUserService.partialUpdate(myAppUser));
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

  previousState(): void {
    window.history.back();
  }
}
