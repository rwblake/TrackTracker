import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { APP_NAME } from '../app.constants';
import { IAppUser } from '../entities/app-user/app-user.model';
import { IUser } from '../entities/user/user.model';
import { IUserPreferences } from '../entities/user-preferences/user-preferences.model';
import { IFeed } from '../entities/feed/feed.model';
import { ICard } from '../entities/card/card.model';
import dayjs from 'dayjs/esm';
import { CardType } from '../entities/enumerations/card-type.model';
import { Account_Combined } from '../account/account_combined.model';
import { Account } from '../core/auth/account.model';
import { error } from '@angular/compiler-cli/src/transformers/util';
import relativeTime from 'dayjs/esm/plugin/relativeTime';
import { FriendsService } from '../friends/friends.service';

import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [style({ opacity: 0 }), animate('0.2s', style({ opacity: 1 }))]),
      transition(':leave', [style({ opacity: 0 })]),
    ]),
  ],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  account_data: Account_Combined | null = null;
  loading = true;

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router, private friendsService: FriendsService) {}

  ngOnInit(): void {
    this.loading = true; // enable the loading ui
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        if (account === null) {
          // If user not logged in (or the account is not authenticated)
          this.account = null;
          this.account_data = null;
          this.loading = false;
        } else {
          // If the account is authenticated
          this.account = account;
          this.accountService
            .fetchAccountCombined()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: appUser => {
                this.account_data = appUser;
                this.loading = false;
              },
              error: () => {
                this.account_data = null;
                this.loading = false;
              },
            });
        }
      });
  }

  gotoLogin(): void {
    this.router.navigate(['/login']);
  }

  gotoProfile() {
    if (this.account_data !== null) {
      this.router.navigate(['/profile/' + this.account_data.internalUser.login]);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  trackByIndex(index: number) {
    return index;
  }

  trackByCardID(card: any) {
    return card.id;
  }

  hasMaxPinned() {
    if (this.account_data === null) return false;

    return this.account_data && this.account_data.pinnedFriends.length >= 5;
  }

  getLastUpdateMessage() {
    if (this.account_data === null) return '';
    dayjs.extend(relativeTime);
    return 'Your music profile was last updated ' + dayjs(this.account_data.feed.lastUpdated).fromNow() + '.';
  }

  // Gets the names of a user, handling is a user's account is not set up properly
  getNames() {
    if (this.account_data) {
      return [this.account_data.internalUser.firstName, this.account_data.internalUser.lastName];
    } else {
      console.warn('This user does not have their account set up properly - Missing AppUser');
      return ['no_name', 'no_name'];
    }
  }

  pin(appUserId: number) {
    this.friendsService.pin(appUserId);
  }

  unpin(appUserId: number) {
    this.friendsService.unpin(appUserId);
  }

  protected readonly APP_NAME = APP_NAME;
}
