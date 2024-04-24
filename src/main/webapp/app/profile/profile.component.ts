import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { Router } from '@angular/router';
import { IAppUser } from '../entities/app-user/app-user.model';
import { takeUntil } from 'rxjs/operators';
import { AccountService } from '../core/auth/account.service';
import { Subject } from 'rxjs';
import { Account } from 'app/core/auth/account.model';
import { PlaylistInsightsService } from '../playlist-insights/playlist-insights.service';
import { HttpClient } from '@angular/common/http';
import { IFriend } from '../friends/friend.model';
import { IPlaylist } from '../entities/playlist/playlist.model';

@Component({
  selector: 'jhi-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  accountError = false;
  userError = false;
  friendsError = false;
  playlistError = false;

  account: Account | null = null;
  user: IAppUser | null = null;
  friends?: IFriend[];
  playlistData?: IPlaylist[];

  appUser: any;
  modal = document.getElementById('myModal');
  btn = document.getElementById('helpBtn');
  span = document.getElementsByClassName('close')[0];
  private readonly destroy$ = new Subject<void>();

  constructor(
    private titleService: Title,
    private router: Router,
    private accountService: AccountService,
    private playlistInsightsService: PlaylistInsightsService,
    protected http: HttpClient
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - My Profile');

    // Retrieve account information
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: account => (this.account = account),
        error: error => {
          this.accountError = true;
          console.error('Error fetching account:', error);
        },
      });

    // Retrieve AppUser information
    this.accountService.fetchUser().subscribe({
      next: (data: IAppUser) => (this.user = data),
      error: error => {
        this.userError = true;
        console.error('Error fetching user bio:', error);
      },
    });

    // Retrieve playlist insights information
    const playlists$ = this.playlistInsightsService.retrieveUserPlaylists();
    playlists$.subscribe({
      next: playlists => {
        if (Array.isArray(playlists)) {
          this.playlistData = playlists;
        } else {
          this.playlistError = true;
          console.error('Data is not an array of playlists');
        }
      },
      error: error => {
        this.playlistError = true;
        console.error('Error fetching playlists:', error);
      },
    });

    // Retrieve friends information
    this.http.get<IFriend[]>('/api/friends').subscribe({
      next: v => (this.friends = v),
      error: error => {
        this.friendsError = true;
        console.error('Error fetching friends:', error);
      },
    });
  }

  async goToPlaylist(id: number) {
    await this.router.navigate(['/insights/playlist'], { queryParams: { playlistID: id } });
  }
}
