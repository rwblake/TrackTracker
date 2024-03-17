import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { LegendPosition } from '@swimlane/ngx-charts';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';
import { AccountService } from '../core/auth/account.service';
import { IAppUser } from '../entities/app-user/app-user.model';
// import {InsightsService} from './insights.'
import { PlaylistInsightsService } from '../playlist-insights/playlist-insights.service';
import { PlaylistInsightsResponse } from '../playlist-insights/playlist-insights-response-interface';

@Component({
  selector: 'jhi-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss'],
})
export class InsightsComponent implements OnInit {
  user: IAppUser | null = null;

  genrePieChartTitle = 'Genres Listened To';
  pieLimit: number = 10;
  genrePieChart: { name: string; value: number }[] = [];
  below = LegendPosition.Below;

  xaxisLabelBar = 'Time period';
  yaxisLabelBar = 'Hours';

  listenedTimeBar: { name: string; value: number }[] = [];

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
  albumsListenedBar: { name: string; value: number }[] = [];

  selectedTimePeriod?: TimePeriod;
  // view: number[] = [];

  // Used to check if data has been pulled
  pulledData: boolean = false;
  waitingForResponse: boolean = false;
  showWaitingMessage: boolean = false;
  showErrorMessage: boolean = false;

  constructor(private titleService: Title, private accountService: AccountService) {}
  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - My Insights');

    this.accountService.fetchUser().subscribe(
      (data: IAppUser) => {
        this.user = data;
      },
      error => {
        console.error('Error fetching user bio:', error);
      }
    );
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
  onTimePeriodChange(period: TimePeriod): void {
    this.selectedTimePeriod = period;
  }

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

  // resizeChart(width: any): void {
  //   this.view = [width, 320]
  // }
}
