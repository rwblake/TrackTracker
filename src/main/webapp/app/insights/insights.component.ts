import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { LegendPosition } from '@swimlane/ngx-charts';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';

@Component({
  selector: 'jhi-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss'],
})
export class InsightsComponent implements OnInit {
  genrePieChartTitle = 'Genres Listened To';
  genrePieChart = [
    {
      name: 'Pop',
      value: 40632,
      extra: {
        code: 'po',
      },
    },
    {
      name: 'Rock',
      value: 50000,
      extra: {
        code: 'rk',
      },
    },
    {
      name: 'Hip Hop',
      value: 36745,
      extra: {
        code: 'hh',
      },
    },
  ];
  below = LegendPosition.Below;

  xaxisLabelBar = 'Time period';
  yaxisLabelBar = 'Hours';

  listenedTimeBar = [
    {
      name: 'Monday',
      value: 40632,
      extra: {
        code: 'de',
      },
    },
    {
      name: 'Tuesday',
      value: 50000,
      extra: {
        code: 'us',
      },
    },
    {
      name: 'Wednesday',
      value: 36745,
      extra: {
        code: 'fr',
      },
    },
    {
      name: 'Thursday',
      value: 36240,
      extra: {
        code: 'uk',
      },
    },
    {
      name: 'Friday',
      value: 33000,
      extra: {
        code: 'es',
      },
    },
    {
      name: 'Saturday',
      value: 35800,
      extra: {
        code: 'it',
      },
    },
  ];

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
  albumsListenedBar = [
    {
      name: 'Hugo',
      value: 40632,
      extra: {
        code: 'lc',
      },
    },
    {
      name: 'Halo',
      value: 50000,
      extra: {
        code: 'bk',
      },
    },
    {
      name: 'Purpose',
      value: 36745,
      extra: {
        code: 'jb',
      },
    },
    {
      name: '30',
      value: 36240,
      extra: {
        code: 'ad',
      },
    },
    {
      name: '25',
      value: 33000,
      extra: {
        code: 'ad',
      },
    },
  ];

  selectedTimePeriod?: TimePeriod;
  // view: number[] = [];

  constructor(private titleService: Title) {}
  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - My Insights');
  }

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
