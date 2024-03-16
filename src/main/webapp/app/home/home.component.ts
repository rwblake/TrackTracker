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

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  account_data: Account_Combined | null = null;
  loading = false;
  //   {
  //   id: 123,
  //   bio: "this is my bio",
  //   spotifyID: "spotifyID",
  //   spotifyUsername: "spotifyUsername",
  //   avatarURL: 'content/images/staticProfile1.png',
  //   internalUser: {
  //     id: 123,
  //     firstName: 'Sam',
  //     lastName: 'Njeri',
  //   },
  //   friends: [
  //     {
  //       firstName: 'Abuya',
  //       lastName: 'Njeri',
  //       createdAt: dayjs(new Date()),
  //       friendID: 1212,
  //       pinned: true,
  //       avatarURL: 'content/images/staticProfile2.png',
  //     },
  //     {
  //       firstName: 'Tom',
  //       lastName: 'Weber',
  //       createdAt: dayjs(new Date()),
  //       friendID: 1212,
  //       pinned: true,
  //       avatarURL: 'content/images/staticProfile3.png',
  //     },
  //   ],
  //   feed: {
  //     id: 1223,
  //     lastUpdated: dayjs(new Date()),
  //     cards: [
  //       {
  //         id: 12002,
  //         belongsTo: {
  //           appUserID: 123,
  //           firstName: 'Sam',
  //           lastName: 'Njeri'
  //         },
  //         metric: CardType.NO_OF_SONGS_LISTENED,
  //         metricValue: 2000, // DANGER: value is a reserved keyword in SQL
  //         timeGenerated: dayjs(new Date())
  //       },
  //       {
  //         id: 12004,
  //         belongsTo: {
  //           appUserID: 123,
  //           firstName: 'Sam',
  //           lastName: 'Njeri'
  //         },
  //         metric: CardType.NO_OF_SONGS_LISTENED,
  //         metricValue: 2000, // DANGER: value is a reserved keyword in SQL
  //         timeGenerated: dayjs(new Date())
  //       }
  //     ]
  //   }
  // };

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router, private friendsService: FriendsService) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        if (account === null) {
          // If user not logged in (or the account is not authenticated)
          this.account = null;
          this.account_data = null;
        } else {
          // If the account is authenticated
          this.account = account;
          this.accountService
            .fetchAccountCombined()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: appUser => (this.account_data = appUser),
              error: () => (this.account_data = null),
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

  // trackByIndex(index: number) {
  //   return index;
  // }

  trackByCardID(card: any) {
    return card.id;
  }

  hasMaxPinned() {
    if (this.account_data === null) return false;

    return this.account_data && this.account_data.pinnedFriends.length >= 5;
  }

  // getLastUpdateMessage(): string {
  //   if (this.account_data === null) return "";
  //
  //   // console.log(this.account_data.feed.lastUpdated)
  //   //
  //   // let seconds = Date.now() - (new Date(this.account_data.feed.lastUpdated)).getTime() / 1000;
  //   // let time;
  //   // let period = 'second';
  //   //
  //   // if (seconds < 60) {
  //   //   time = Math.floor((Date.now() - (new Date(this.account_data.feed.lastUpdated)).getTime()) / (1000 * 60));
  //   //   period = 'minute';
  //   // } else if (seconds < 60 * 60) {
  //   //   time = Math.floor((Date.now() - (new Date(this.account_data.feed.lastUpdated)).getTime()) / (1000 * 60 * 60));
  //   //   period = 'hour';
  //   // } else if (seconds < 60 * 60) {
  //   //   time = Math.floor((Date.now() - (new Date(this.account_data.feed.lastUpdated)).getTime()) / (1000 * 60 * 60 * 24));
  //   //   period = 'day';
  //   // } else {
  //   //   time = Math.floor((Date.now() - (new Date(this.account_data.feed.lastUpdated)).getTime()) / (1000 * 60 * 60 * 24 * 7));
  //   //   period = 'week';
  //   // }
  //   //
  //   // if (time > 1) period += 's';
  //   //
  //   // return 'Your music profile was last updated ' + time + ' ' + period + ' ago.';
  //
  //   dayjs.extend(relativeTime)
  //   console.log(this.account_data.feed.lastUpdated)
  //   return dayjs(this.account_data.feed.lastUpdated).fromNow();
  // }

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
