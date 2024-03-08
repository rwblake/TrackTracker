import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';
import { FriendsInsightsService } from './friends-insights.service';
import { IPopularCategories } from './friends-insights.model';
import { Account } from '../core/auth/account.model';
import { AccountService } from '../core/auth/account.service';
import { CardStackData } from '../card-stack/card-stack.component';

@Component({
  selector: 'jhi-friends-insights',
  templateUrl: './friends-insights.component.html',
  styleUrls: ['./friends-insights.component.scss'],
})
export class FriendsInsightsComponent implements OnInit {
  selectedTimePeriod?: TimePeriod;
  error = false;

  account: Account | null = null;

  popularCategories?: IPopularCategories;

  sampleData: CardStackData[] = [
    {
      image_url: 'https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228',
      title: 'Eminem',
      description: '12,325 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d0000b273282b9210352afd2c657ec103',
      title: 'Billie Eilish',
      description: '10,619 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d00001e0234f21d3047d85440dfa37f10',
      title: 'Ariana Grande',
      description: '4,097 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d00001e02451dc79ccf6dfaa6cc5ce029',
      title: 'I DONT KNOW HOW BUT THEY FOUND ME',
      description: '1,290 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228',
      title: 'Eminem',
      description: '12,325 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d0000b273282b9210352afd2c657ec103',
      title: 'Billie Eilish',
      description: '10,619 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d00001e0234f21d3047d85440dfa37f10',
      title: 'Ariana Grande',
      description: '4,097 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d00001e02451dc79ccf6dfaa6cc5ce029',
      title: 'I DONT KNOW HOW BUT THEY FOUND ME',
      description: '1,290 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d0000b273282b9210352afd2c657ec103',
      title: 'Billie Eilish',
      description: '10,619 Streams',
    },
    {
      image_url: 'https://i.scdn.co/image/ab67616d00001e0234f21d3047d85440dfa37f10',
      title: 'Ariana Grande',
      description: '4,097 Streams',
    },
  ];

  constructor(
    private titleService: Title,
    private accountService: AccountService,
    private friendsInsightsService: FriendsInsightsService
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Friend Insights');

    this.accountService
      .getAuthenticationState()
      // .pipe(takeUntil(this.destroy$))
      .subscribe(account => this.onAuthenticated(account));
  }

  onAuthenticated(account: Account | null) {
    this.account = account;
    this.popularCategories = undefined;

    if (!account) return;
  }

  private requestPopularCategories() {
    this.popularCategories = undefined;

    this.friendsInsightsService.getPopularCategories(this.selectedTimePeriod?.periodDays).subscribe({
      next: res => (this.popularCategories = res),
      error: () => (this.error = true),
    });
  }

  onTimePeriodChange(period: TimePeriod) {
    this.selectedTimePeriod = period;
    this.requestPopularCategories();
  }
}
