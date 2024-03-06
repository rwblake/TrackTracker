import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { APP_NAME } from '../app.constants';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  customAccount = {
    firstName: 'Sam',
    lastName: 'Njeri',
    avatarURL: 'content/images/staticProfile1.png',
    last_update: new Date(2024, 1, 25, 0, 0, 0),
    friends: [
      {
        firstName: 'Abuya',
        lastName: 'Njeri',
        pinned: true,
        avatarURL: 'content/images/staticProfile2.png',
      },
      {
        firstName: 'Tom',
        lastName: 'Weber',
        pinned: true,
        avatarURL: 'content/images/staticProfile3.png',
      },
    ],
  };

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
  }

  gotoLogin(): void {
    this.router.navigate(['/login']);
  }

  gotoProfile() {
    if (this.account !== null) {
      this.router.navigate(['/profile/' + this.account.login]);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  trackByIndex(index: number) {
    return index;
  }

  hasMaxPinned() {
    return this.customAccount.friends.filter(friend => friend.pinned).length >= 4;
  }

  getLastUpdateMessage(): string {
    let seconds = Date.now() - this.customAccount.last_update.getTime() / 1000;
    let time;
    let period = 'second';

    if (seconds < 60) {
      time = Math.floor((Date.now() - this.customAccount.last_update.getTime()) / (1000 * 60));
      period = 'minute';
    } else if (seconds < 60 * 60) {
      time = Math.floor((Date.now() - this.customAccount.last_update.getTime()) / (1000 * 60 * 60));
      period = 'hour';
    } else if (seconds < 60 * 60) {
      time = Math.floor((Date.now() - this.customAccount.last_update.getTime()) / (1000 * 60 * 60 * 24));
      period = 'day';
    } else {
      time = Math.floor((Date.now() - this.customAccount.last_update.getTime()) / (1000 * 60 * 60 * 24 * 7));
      period = 'week';
    }

    if (time > 1) period += 's';

    return 'Your music profile was last updated ' + time + ' ' + period + ' ago.';
  }

  protected readonly APP_NAME = APP_NAME;
}
