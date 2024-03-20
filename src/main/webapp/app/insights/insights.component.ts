import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { Color, LegendPosition, ScaleType } from '@swimlane/ngx-charts';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';
import { AccountService } from '../core/auth/account.service';
import { IAppUser } from '../entities/app-user/app-user.model';
import { InsightsService } from './insights.service';
import {
  ArtistMapResponse,
  InsightsResponse,
  YearMapResponse,
  GenreMapResponse,
  GraphDataResponse,
  AlbumMapResponse,
  StreamInsightsResponse,
  Entry,
} from './insights-response-interface';
import { IPlaylist } from '../entities/playlist/playlist.model';
import { IStream } from '../entities/stream/stream.model';
import { error } from '@angular/compiler-cli/src/transformers/util';

@Component({
  selector: 'jhi-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss'],
})
export class InsightsComponent implements OnInit {
  user: IAppUser | null = null;

  recentStreams: IStream[] = [];
  hasStreams: boolean = false;

  pieLimit: number = 10;

  showByWeek: boolean = true;
  showByMonth: boolean = true;
  showByYear: boolean = true;
  showByAllTime: boolean = true;

  below = LegendPosition.Below;

  chartScheme: Color = {
    name: 'pieSchemeA',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#7825BC', '#B237EC', '#EA3AC1', '#EA002B', '#FF3407', '#FF6528', '#FF8E16', '#FFB219', '#FFD54C', '#FFEFA3'],
  };

  genrePieChartWeek: { name: string; value: number }[] = [];
  genrePieChartMonth: { name: string; value: number }[] = [];
  genrePieChartYear: { name: string; value: number }[] = [];
  genrePieChartAllTime: { name: string; value: number }[] = [];

  genrePie: { name: string; value: number }[] = [];

  albumsListenedBarWeek: { name: string; value: number }[] = [];
  albumsListenedBarMonth: { name: string; value: number }[] = [];
  albumsListenedBarYear: { name: string; value: number }[] = [];
  albumsListenedBarAllTime: { name: string; value: number }[] = [];

  albumsBar: { name: string; value: number }[] = [];

  artistsListenedLineWeek: { name: string; value: number }[] = [];
  artistsListenedLineMonth: { name: string; value: number }[] = [];
  artistsListenedLineYear: { name: string; value: number }[] = [];
  artistsListenedLineAllTime: { name: string; value: number }[] = [];

  artistsLine: { name: string; value: number }[] = [];

  songChartWeek: { name: string; value: number }[] = [];
  songChartMonth: { name: string; value: number }[] = [];
  songChartYear: { name: string; value: number }[] = [];
  songChartAllTime: { name: string; value: number }[] = [];

  songChart: { name: string; value: number }[] = [];

  decadeChartWeek: { name: string; value: number }[] = [];
  decadeChartMonth: { name: string; value: number }[] = [];
  decadeChartYear: { name: string; value: number }[] = [];
  decadeChartAllTime: { name: string; value: number }[] = [];

  decadeChart: { name: string; value: number }[] = [];

  selectedTimePeriod?: TimePeriod;
  // view: number[] = [];

  // Used to check if data has been pulled
  pulledData: boolean = false;
  waitingForResponse: boolean = false;
  showWaitingMessage: boolean = false;
  showErrorMessage: boolean = false;

  constructor(private titleService: Title, private accountService: AccountService, private insightsService: InsightsService) {}
  response: StreamInsightsResponse | undefined;

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - My Insights');

    // get the user
    this.accountService.fetchUser().subscribe({
      next: data => (this.user = data),
      error: error => console.error('Error fetching user bio:', error),
    });

    // get the insights to populate the graphs
    this.insightsService.retrieveStreamInsights().subscribe({
      next: response => {
        if (response === null) console.error('No response given');
        else this.onStreamRetrievalSuccess(response);
      },
      error: e => console.error(e),
      // this.onStreamRetrievalFailure(e),
    });
  }

  // onStreamRetrievalFailure(errors:any){
  //   this.showErrorMessage = true;
  // }

  onStreamRetrievalSuccess(val: StreamInsightsResponse) {
    this.response = val;
    console.log('onStreamRetrievalSuccess', val);
    this.addGenresToChartWeek(this.response.genreCounter.byWeek);
    this.addGenresToChartMonth(this.response.genreCounter.byMonth);
    this.addGenresToChartYear(this.response.genreCounter.byYear);
    this.addGenresToChartAllTime(this.response.genreCounter.ofAllTime);
    this.addAlbumsToChartWeek(this.response.albumCounter.byWeek);
    this.addAlbumsToChartMonth(this.response.albumCounter.byMonth);
    this.addAlbumsToChartYear(this.response.albumCounter.byYear);
    this.addAlbumsToChartAllTime(this.response.albumCounter.ofAllTime);
    this.addArtistsToChartWeek(this.response.artistCounter.byWeek);
    this.addArtistsToChartMonth(this.response.artistCounter.byMonth);
    this.addArtistsToChartYear(this.response.artistCounter.byYear);
    this.addArtistsToChartAllTime(this.response.artistCounter.ofAllTime);
    this.addSongToChartWeek(this.response.songCounter.byWeek);
    this.addSongToChartMonth(this.response.songCounter.byMonth);
    this.addSongToChartYear(this.response.songCounter.byYear);
    this.addSongToChartAllTime(this.response.songCounter.ofAllTime);
    this.addDecToChartWeek(this.response.songCounter.byWeek);
    this.addDecToChartMonth(this.response.songCounter.byMonth);
    this.addDecToChartYear(this.response.songCounter.byYear);
    this.addDecToChartAllTime(this.response.songCounter.ofAllTime);
    this.albumsBar = this.albumsListenedBarWeek;
    this.genrePie = this.genrePieChartWeek;
    this.artistsLine = this.artistsListenedLineWeek;
    this.decadeChart = this.decadeChartWeek;
    this.songChart = this.songChartWeek;
  }
  // constructor(private titleService: Title, private insightsService: InsightsService) {}
  // response: InsightsResponse | undefined;
  // async sendSpotID() {
  //   // @ts-ignore
  //   const spotid: String = data;
  //
  //     this.waitingForResponse = true;
  //   this.pulledData = false;
  //   this.showErrorMessage = false;
  //
  //   this.InsightsService.sendURL(url).subscribe({
  //     next: v => this.onSuccessfulResponse(v),
  //     error: e => this.onFailure(e),
  //   });
  //
  //   // Pulling data is sometimes effectively instant. It looks bad if the "waiting" message pops up
  //   // for half a second - so we only show it if we've been waiting for some time.
  //   await this.delay(750);
  //   if (this.waitingForResponse) {
  //     this.showWaitingMessage = true;
  //   }
  // }

  addDecToChartWeek(decadeToCounts: Entry[]) {
    this.decadeChartWeek = [];

    for (let i: number = decadeToCounts.length - 1; i >= 0; i--) {
      if (i < decadeToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof decadeToCounts[i].metric === 'string'
          ? decadeToCounts[i].metric
          : // @ts-ignore
            decadeToCounts[i].metric.name;

      this.decadeChartWeek.push({
        name: name,
        value: decadeToCounts[i].value,
      });
    }
  }
  addDecToChartMonth(decadeToCounts: Entry[]) {
    this.decadeChartMonth = [];

    for (let i: number = decadeToCounts.length - 1; i >= 0; i--) {
      if (i < decadeToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof decadeToCounts[i].metric === 'string'
          ? decadeToCounts[i].metric
          : // @ts-ignore
            decadeToCounts[i].metric.name;

      this.decadeChartMonth.push({
        name: name,
        value: decadeToCounts[i].value,
      });
    }
  }
  addDecToChartYear(decadeToCounts: Entry[]) {
    this.decadeChartYear = [];

    for (let i: number = decadeToCounts.length - 1; i >= 0; i--) {
      if (i < decadeToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof decadeToCounts[i].metric === 'string'
          ? decadeToCounts[i].metric
          : // @ts-ignore
            decadeToCounts[i].metric.name;

      this.decadeChartYear.push({
        name: name,
        value: decadeToCounts[i].value,
      });
    }
  }
  addDecToChartAllTime(decadeToCounts: Entry[]) {
    this.decadeChartAllTime = [];

    for (let i: number = decadeToCounts.length - 1; i >= 0; i--) {
      if (i < decadeToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof decadeToCounts[i].metric === 'string'
          ? decadeToCounts[i].metric
          : // @ts-ignore
            decadeToCounts[i].metric.name;

      this.decadeChartAllTime.push({
        name: name,
        value: decadeToCounts[i].value,
      });
    }
  }
  addSongToChartWeek(songsToCounts: Entry[]) {
    this.songChartWeek = [];

    for (let i: number = songsToCounts.length - 1; i >= 0; i--) {
      if (i < songsToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof songsToCounts[i].metric === 'string'
          ? songsToCounts[i].metric
          : // @ts-ignore
            songsToCounts[i].metric.name;

      this.songChartWeek.push({
        name: name,
        value: songsToCounts[i].value,
      });
    }
  }
  addSongToChartMonth(songsToCounts: Entry[]) {
    this.songChartMonth = [];

    for (let i: number = songsToCounts.length - 1; i >= 0; i--) {
      if (i < songsToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof songsToCounts[i].metric === 'string'
          ? songsToCounts[i].metric
          : // @ts-ignore
            songsToCounts[i].metric.name;

      this.songChartMonth.push({
        name: name,
        value: songsToCounts[i].value,
      });
    }
  }
  addSongToChartYear(songsToCounts: Entry[]) {
    this.songChartYear = [];

    for (let i: number = songsToCounts.length - 1; i >= 0; i--) {
      if (i < songsToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof songsToCounts[i].metric === 'string'
          ? songsToCounts[i].metric
          : // @ts-ignore
            songsToCounts[i].metric.name;

      this.songChartYear.push({
        name: name,
        value: songsToCounts[i].value,
      });
    }
  }
  addSongToChartAllTime(songsToCounts: Entry[]) {
    this.songChartAllTime = [];

    for (let i: number = songsToCounts.length - 1; i >= 0; i--) {
      if (i < songsToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof songsToCounts[i].metric === 'string'
          ? songsToCounts[i].metric
          : // @ts-ignore
            songsToCounts[i].metric.name;

      this.songChartAllTime.push({
        name: name,
        value: songsToCounts[i].value,
      });
    }
  }

  addGenresToChartWeek(genresToCounts: Entry[]) {
    this.genrePieChartWeek = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof genresToCounts[i].metric === 'string'
          ? genresToCounts[i].metric
          : // @ts-ignore
            genresToCounts[i].metric.name;

      this.genrePieChartWeek.push({
        name: name,
        value: genresToCounts[i].value,
      });
    }
  }

  addGenresToChartMonth(genresToCounts: Entry[]) {
    this.genrePieChartMonth = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof genresToCounts[i].metric === 'string'
          ? genresToCounts[i].metric
          : // @ts-ignore
            genresToCounts[i].metric.name;

      this.genrePieChartMonth.push({
        name: name,
        value: genresToCounts[i].value,
      });
    }
  }

  addGenresToChartYear(genresToCounts: Entry[]) {
    this.genrePieChartYear = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof genresToCounts[i].metric === 'string'
          ? genresToCounts[i].metric
          : // @ts-ignore
            genresToCounts[i].metric.name;

      this.genrePieChartYear.push({
        name: name,
        value: genresToCounts[i].value,
      });
    }
  }

  addGenresToChartAllTime(genresToCounts: Entry[]) {
    this.genrePieChartAllTime = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }

      const name =
        typeof genresToCounts[i].metric === 'string'
          ? genresToCounts[i].metric
          : // @ts-ignore
            genresToCounts[i].metric.name;

      this.genrePieChartAllTime.push({
        name: name,
        value: genresToCounts[i].value,
      });
    }
  }

  addAlbumsToChartWeek(albumsToProportions: Entry[]) {
    this.albumsListenedBarWeek = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof albumsToProportions[i].metric === 'string'
          ? albumsToProportions[i].metric
          : // @ts-ignore
            albumsToProportions[i].metric.name;

      this.albumsListenedBarWeek.push({
        name: name,
        value: albumsToProportions[i].value,
      });
    }
  }

  addAlbumsToChartMonth(albumsToProportions: Entry[]) {
    this.albumsListenedBarMonth = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof albumsToProportions[i].metric === 'string'
          ? albumsToProportions[i].metric
          : // @ts-ignore
            albumsToProportions[i].metric.name;

      this.albumsListenedBarMonth.push({
        name: name,
        value: albumsToProportions[i].value,
      });
    }
  }

  addAlbumsToChartYear(albumsToProportions: Entry[]) {
    this.albumsListenedBarYear = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof albumsToProportions[i].metric === 'string'
          ? albumsToProportions[i].metric
          : // @ts-ignore
            albumsToProportions[i].metric.name;

      this.albumsListenedBarYear.push({
        name: name,
        value: albumsToProportions[i].value,
      });
    }
  }

  addAlbumsToChartAllTime(albumsToProportions: Entry[]) {
    this.albumsListenedBarAllTime = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof albumsToProportions[i].metric === 'string'
          ? albumsToProportions[i].metric
          : // @ts-ignore
            albumsToProportions[i].metric.name;

      this.albumsListenedBarAllTime.push({
        name: name,
        value: albumsToProportions[i].value,
      });
    }
  }
  addArtistsToChartWeek(artiststToProportions: Entry[]) {
    this.artistsListenedLineWeek = [];

    for (let i = artiststToProportions.length - 1; i >= 0; i--) {
      if (i < artiststToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof artiststToProportions[i].metric === 'string'
          ? artiststToProportions[i].metric
          : // @ts-ignore
            artiststToProportions[i].metric.name;

      this.artistsListenedLineWeek.push({
        name: name,
        value: artiststToProportions[i].value,
      });
    }
  }
  addArtistsToChartMonth(artiststToProportions: Entry[]) {
    this.artistsListenedLineMonth = [];

    for (let i = artiststToProportions.length - 1; i >= 0; i--) {
      if (i < artiststToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof artiststToProportions[i].metric === 'string'
          ? artiststToProportions[i].metric
          : // @ts-ignore
            artiststToProportions[i].metric.name;

      this.albumsListenedBarMonth.push({
        name: name,
        value: artiststToProportions[i].value,
      });
    }
  }
  addArtistsToChartYear(artiststToProportions: Entry[]) {
    this.artistsListenedLineYear = [];

    for (let i = artiststToProportions.length - 1; i >= 0; i--) {
      if (i < artiststToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof artiststToProportions[i].metric === 'string'
          ? artiststToProportions[i].metric
          : // @ts-ignore
            artiststToProportions[i].metric.name;

      this.albumsListenedBarYear.push({
        name: name,
        value: artiststToProportions[i].value,
      });
    }
  }
  addArtistsToChartAllTime(artiststToProportions: Entry[]) {
    this.artistsListenedLineAllTime = [];

    for (let i = artiststToProportions.length - 1; i >= 0; i--) {
      if (i < artiststToProportions.length - this.pieLimit) {
        break;
      }

      const name =
        typeof artiststToProportions[i].metric === 'string'
          ? artiststToProportions[i].metric
          : // @ts-ignore
            artiststToProportions[i].metric.name;

      this.albumsListenedBarAllTime.push({
        name: name,
        value: artiststToProportions[i].value,
      });
    }
  }

  onTimePeriodChange(period: TimePeriod): void {
    this.selectedTimePeriod = period;
    this.showByWeek = period.label === 'Week';
    this.showByMonth = period.label === '4 Weeks';
    this.showByYear = period.label === 'Year';
    this.showByAllTime = period.label === 'All Time';
    this.albumsBar = [];
    this.decadeChart = [];
    this.genrePie = [];
    this.songChart = [];
    this.artistsLine = [];

    if (period.label === 'Week') {
      this.albumsBar = this.albumsListenedBarWeek;
      this.genrePie = this.genrePieChartWeek;
      this.artistsLine = this.artistsListenedLineWeek;
      this.decadeChart = this.decadeChartWeek;
      this.songChart = this.songChartWeek;
    }
    if (period.label === '4 Weeks') {
      this.albumsBar = this.albumsListenedBarMonth;
      this.genrePie = this.genrePieChartMonth;
      this.artistsLine = this.artistsListenedLineMonth;
      this.decadeChart = this.decadeChartMonth;
      this.songChart = this.songChartMonth;
    }
    if (period.label === 'Year') {
      this.albumsBar = this.albumsListenedBarYear;
      this.genrePie = this.genrePieChartYear;
      this.artistsLine = this.artistsListenedLineYear;
      this.decadeChart = this.decadeChartYear;
      this.songChart = this.songChartYear;
    }
    if (period.label === 'All Time') {
      this.albumsBar = this.albumsListenedBarAllTime;
      this.genrePie = this.genrePieChartAllTime;
      this.artistsLine = this.artistsListenedLineAllTime;
      this.decadeChart = this.decadeChartAllTime;
      this.songChart = this.songChartAllTime;
    }
  }

  // async sendTime(){
  //   this.
  // }

  graphNameChange(): string {
    switch (this.selectedTimePeriod?.label) {
      case 'Week':
        return 'Here are your stats for this Week';
      case '4 Weeks':
        return 'Here are your stats over 4 Weeks';
      case 'Year':
        return 'Here are your stats over a Year';
      default:
        return 'Here are your all time stats';
    }
  }

  leaderNameChange(): string {
    switch (this.selectedTimePeriod?.label) {
      case 'Week':
        return 'Lets look at your leaderboard for this Week';
      case '4 Weeks':
        return 'Lets look at your leaderboard over 4 Weeks';
      case 'Year':
        return 'Lets look at your leaderboard over a Year';
      default:
        return 'Here are your all time leaderboards';
    }
  }
  onRecentlyViewedRetrievalSuccess(val: IStream[]) {
    this.recentStreams = val;
    this.hasStreams = this.recentStreams.length != 0;
  }

  // resizeChart(width: any): void {
  //   this.view = [width, 320]
  // }
}
