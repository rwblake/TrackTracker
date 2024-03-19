import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';
import { FriendsInsightsService } from './friends-insights.service';
import { CategoryInformation, IPopularCategories } from './friends-insights.model';
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

  private categoryInformationToCardStackData(data: CategoryInformation): CardStackData {
    return {
      image_url: data.value.imageURL,
      title: data.value.name,
      description: `${data.frequency} Streams`,
    };
  }

  getArtistCardStackData(): CardStackData[] | undefined {
    return this.popularCategories?.artists.map(this.categoryInformationToCardStackData);
  }

  getTracksCardStackData(): CardStackData[] | undefined {
    return this.popularCategories?.tracks.map(this.categoryInformationToCardStackData);
  }

  getAlbumsCardStackData(): CardStackData[] | undefined {
    return this.popularCategories?.albums.map(this.categoryInformationToCardStackData);
  }
}
