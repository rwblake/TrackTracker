import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { LegendPosition } from '@swimlane/ngx-charts';
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

  genrePieChartTitle = 'Genres Listened To';
  pieLimit: number = 10;

  below = LegendPosition.Below;

  xaxisLabelBar = 'Time period';
  yaxisLabelBar = 'Hours';

  listenedTimeBarWeek: { name: string; value: number }[] = [];
  listenedTimeBarMonth: { name: string; value: number }[] = [];
  listenedTimeBarYear: { name: string; value: number }[] = [];
  listenedTimeBarAllTime: { name: string; value: number }[] = [];

  genrePieChartWeek: { name: string; value: number }[] = [];
  genrePieChartMonth: { name: string; value: number }[] = [];
  genrePieChartYear: { name: string; value: number }[] = [];
  genrePieChartAllTime: { name: string; value: number }[] = [];

  albumsListenedBarWeek: { name: string; value: number }[] = [];
  albumsListenedBarMonth: { name: string; value: number }[] = [];
  albumsListenedBarYear: { name: string; value: number }[] = [];
  albumsListenedBarAllTime: { name: string; value: number }[] = [];

  yaxisLabelLine = 'Hours';
  artistLineChartTitle = 'Artists';
  artistTrendLine = [
    {
      name: 'Ye',
      series: [
        {
          value: 5361,
          name: '2016-09-14T23:29:01.071Z',
        },
        {
          value: 3341,
          name: '2016-09-13T08:28:01.209Z',
        },
        {
          value: 6061,
          name: '2016-09-16T12:47:22.021Z',
        },
        {
          value: 4908,
          name: '2016-09-17T09:20:13.122Z',
        },
        {
          value: 4610,
          name: '2016-09-20T04:00:11.673Z',
        },
      ],
    },
    {
      name: 'Bob Marley',
      series: [
        {
          value: 3553,
          name: '2016-09-14T23:29:01.071Z',
        },
        {
          value: 3010,
          name: '2016-09-13T08:28:01.209Z',
        },
        {
          value: 5654,
          name: '2016-09-16T12:47:22.021Z',
        },
        {
          value: 4126,
          name: '2016-09-17T09:20:13.122Z',
        },
        {
          value: 6917,
          name: '2016-09-20T04:00:11.673Z',
        },
      ],
    },
    {
      name: 'Beyonce',
      series: [
        {
          value: 5138,
          name: '2016-09-14T23:29:01.071Z',
        },
        {
          value: 2450,
          name: '2016-09-13T08:28:01.209Z',
        },
        {
          value: 2911,
          name: '2016-09-16T12:47:22.021Z',
        },
        {
          value: 5931,
          name: '2016-09-17T09:20:13.122Z',
        },
        {
          value: 4958,
          name: '2016-09-20T04:00:11.673Z',
        },
      ],
    },
    {
      name: 'The Red Hot Chilli Peppers',
      series: [
        {
          value: 2415,
          name: '2016-09-14T23:29:01.071Z',
        },
        {
          value: 4848,
          name: '2016-09-13T08:28:01.209Z',
        },
        {
          value: 3448,
          name: '2016-09-16T12:47:22.021Z',
        },
        {
          value: 3079,
          name: '2016-09-17T09:20:13.122Z',
        },
        {
          value: 5848,
          name: '2016-09-20T04:00:11.673Z',
        },
      ],
    },
  ];

  xaxisLabelBarAlbum = 'Albums';
  yaxisLableBarAlbum = 'Frequency';
  albumsListenedBar: { name: string; value: number }[] = [];

  selectedTimePeriod?: TimePeriod;
  // view: number[] = [];

  // Used to check if data has been pulled
  pulledData: boolean = false;
  waitingForResponse: boolean = false;
  showWaitingMessage: boolean = false;
  showErrorMessage: boolean = false;

  constructor(private titleService: Title, private accountService: AccountService, private insightsService: InsightsService) {}
  response: InsightsResponse | undefined;

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
        else console.log(response);
      },
      error: e => console.error(e),
      // this.onStreamRetrievalFailure(e),
    });
  }

  // onStreamRetrievalFailure(errors:any){
  //   this.showErrorMessage = true;
  // }

  onStreamRetrievalSuccess(val: InsightsResponse) {
    this.response = val;
    this.addGenresToChartWeek(this.response.graphData.genreMapsWeek);
    this.addGenresToChartMonth(this.response.graphData.genreMapsMonth);
    this.addGenresToChartYear(this.response.graphData.genreMapsYear);
    this.addGenresToChartAllTime(this.response.graphData.genreMapsAllTime);
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

  addGenresToChartWeek(genresToCounts: GenreMapResponse[]) {
    this.genrePieChartWeek = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }
      this.genrePieChartWeek.push({
        name: genresToCounts[i].genreName,
        value: genresToCounts[i].occurrencesInPlaylist,
      });
    }
  }
  addGenresToChartMonth(genresToCounts: GenreMapResponse[]) {
    this.genrePieChartMonth = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }
      this.genrePieChartMonth.push({
        name: genresToCounts[i].genreName,
        value: genresToCounts[i].occurrencesInPlaylist,
      });
    }
  }
  addGenresToChartYear(genresToCounts: GenreMapResponse[]) {
    this.genrePieChartYear = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }
      this.genrePieChartYear.push({
        name: genresToCounts[i].genreName,
        value: genresToCounts[i].occurrencesInPlaylist,
      });
    }
  }
  addGenresToChartAllTime(genresToCounts: GenreMapResponse[]) {
    this.genrePieChartAllTime = [];

    for (let i: number = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }
      this.genrePieChartAllTime.push({
        name: genresToCounts[i].genreName,
        value: genresToCounts[i].occurrencesInPlaylist,
      });
    }
  }

  addAlbumsToChartWeek(albumsToProportions: AlbumMapResponse[]) {
    this.albumsListenedBarWeek = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }
      this.albumsListenedBarWeek.push({
        name: albumsToProportions[i].albumName,
        value: albumsToProportions[i].occurrencesInPlaylist,
      });
    }
  }
  addAlbumsToChartMonth(albumsToProportions: AlbumMapResponse[]) {
    this.albumsListenedBarMonth = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }
      this.albumsListenedBarMonth.push({
        name: albumsToProportions[i].albumName,
        value: albumsToProportions[i].occurrencesInPlaylist,
      });
    }
  }
  addAlbumsToChartYear(albumsToProportions: AlbumMapResponse[]) {
    this.albumsListenedBarYear = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }
      this.albumsListenedBarYear.push({
        name: albumsToProportions[i].albumName,
        value: albumsToProportions[i].occurrencesInPlaylist,
      });
    }
  }
  addAlbumsToChartAllTime(albumsToProportions: AlbumMapResponse[]) {
    this.albumsListenedBarAllTime = [];

    for (let i = albumsToProportions.length - 1; i >= 0; i--) {
      if (i < albumsToProportions.length - this.pieLimit) {
        break;
      }
      this.albumsListenedBarAllTime.push({
        name: albumsToProportions[i].albumName,
        value: albumsToProportions[i].occurrencesInPlaylist,
      });
    }
  }

  onTimePeriodChange(period: TimePeriod): void {
    this.selectedTimePeriod = period;
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
