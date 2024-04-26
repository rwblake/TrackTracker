import { ChangeDetectorRef, Component, NgZone, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY, forkJoin, Subject, throwError } from 'rxjs';
import { catchError, takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { APP_NAME } from '../app.constants';
import dayjs from 'dayjs/esm';
import { Account_Combined, Friendship } from '../account/account_combined.model';
import { Account } from '../core/auth/account.model';
import relativeTime from 'dayjs/esm/plugin/relativeTime';
import { FriendsService } from '../friends/friends.service';

import { trigger, style, transition, animate } from '@angular/animations';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

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
  modalRef: NgbModalRef | undefined;
  modalPinned: Friendship[] = [];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private router: Router,
    private friendsService: FriendsService,
    private modalService: NgbModal,
    private cdRef: ChangeDetectorRef,
    private zone: NgZone
  ) {}

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
                console.log(appUser);
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

  pin(pinnedIds: number[]) {
    return this.friendsService.pin(pinnedIds);
  }

  unpin(unpinnedIds: number[]) {
    return this.friendsService.unpin(unpinnedIds);
  }

  getPinnedFriends(): Friendship[] {
    if (this.account_data) {
      return this.account_data.friends.filter(friend => friend.pinned);
    }
    return [];
  }

  openModal(content: TemplateRef<any>) {
    if (!this.account_data) return;
    this.modalPinned = [...this.account_data.friends]; // update the pinned state
    this.modalRef = this.modalService.open(content, { scrollable: true });
  }

  savePinnedFriends() {
    if (!this.modalRef || !this.account_data) return;

    const unpinned = this.modalPinned.filter(f => !f.pinned).map(f => f.friendID);
    const pinned = this.modalPinned.filter(f => f.pinned).map(f => f.friendID);

    forkJoin([this.pin(pinned), this.unpin(unpinned)]).subscribe({
      next: () => {
        // Both pinning and unpinning completed successfully
        console.log('Pinning and unpinning completed successfully');
        // Call ngOnInit after both operations are completed
        this.ngOnInit();
        this.modalRef!.close('Save click');
      },
      error: err => {
        // Handle errors for both pinning and unpinning
        console.error('Error:', err);
        this.modalRef!.close('Save click');
      },
    });
  }

  protected readonly APP_NAME = APP_NAME;
}
