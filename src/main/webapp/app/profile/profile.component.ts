import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { Router } from '@angular/router';
import { LoginService } from 'app/login/login.service';
import { IAppUser } from '../entities/app-user/app-user.model';
import { takeUntil } from 'rxjs/operators';
import { AccountService } from '../core/auth/account.service';
import { Observable, Subject } from 'rxjs';
import { Account } from 'app/core/auth/account.model';
import { PlaylistService } from '../entities/playlist/service/playlist.service';
import { PlaylistInsightsService } from '../playlist-insights/playlist-insights.service';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { IFriend } from '../friends/friend.model';

@Component({
  selector: 'jhi-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  playlistData: any[] = [];
  friends?: IFriend[];
  user: IAppUser | null = null;
  appUser: any;
  modal = document.getElementById('myModal');
  btn = document.getElementById('helpBtn');
  span = document.getElementsByClassName('close')[0];
  private readonly destroy$ = new Subject<void>();
  account: Account | null = null;

  constructor(
    private titleService: Title,
    private router: Router,
    private accountService: AccountService,
    private playlistInsightsService: PlaylistInsightsService,
    protected http: HttpClient
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - My Profile');
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
    const playlists$ = this.playlistInsightsService.retrieveUserPlaylists();
    playlists$.subscribe(playlists => {
      if (Array.isArray(playlists)) {
        playlists.forEach(playlist => {
          console.log(playlist);
          this.playlistData.push(playlist);
        });
      } else {
        console.error('Data is not an array of playlists');
      }
    });
    this.http.get<IFriend[]>('/api/friends').subscribe({ next: v => (this.friends = v) });
  }

  goToPlaylist(id: number) {
    this.router.navigate(['/insights/playlist'], { queryParams: { playlistID: id } });
  }
}
