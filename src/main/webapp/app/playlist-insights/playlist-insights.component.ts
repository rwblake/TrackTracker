import { Component, NgModule, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { PlaylistInsightsService } from './playlist-insights.service';
import { PlaylistInsightsResponse } from './playlist-insights-response-interface';
import { InsightsComponent } from '../insights/insights.component';
import { CommonModule } from '@angular/common';
import { InsightsRoutingModule } from '../insights/insights-routing.module';
import { LegendPosition, NgxChartsModule } from '@swimlane/ngx-charts';
import { SharedModule } from '../shared/shared.module';

@Component({
  selector: 'jhi-playlist-insights',
  templateUrl: './playlist-insights.component.html',
  styleUrls: ['./playlist-insights.component.scss'],
})
export class PlaylistInsightsComponent implements OnInit {
  urlForm: FormGroup = new FormGroup({ name: new FormControl('', [Validators.required]) });
  linkInput: any;

  artistsPieChartTitle = 'Artists Listened To';
  artistsPieChart = [
    {
      name: 'Loyle Carner',
      value: 40632,
      extra: {
        code: 'lc',
      },
    },
    {
      name: 'Kanye',
      value: 50000,
      extra: {
        code: 'ky',
      },
    },
    {
      name: 'Bob Marley',
      value: 36745,
      extra: {
        code: 'bm',
      },
    },
  ];

  songsPieChartTitle = 'Songs Listened To';
  songsPieChart = [
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

  xaxisLabelBar = 'Time period';
  yaxisLabelBar = 'Hours';
  below = LegendPosition.Below;
  timeperiodBar = [
    {
      name: '1950s',
      value: 40632,
      extra: {
        code: 'de',
      },
    },
    {
      name: '1960s',
      value: 50000,
      extra: {
        code: 'us',
      },
    },
    {
      name: '1970s',
      value: 36745,
      extra: {
        code: 'fr',
      },
    },
    {
      name: '1980s',
      value: 36240,
      extra: {
        code: 'uk',
      },
    },
    {
      name: '1990s',
      value: 50000,
      extra: {
        code: 'us',
      },
    },
    {
      name: '2000s',
      value: 50000,
      extra: {
        code: 'us',
      },
    },
  ];

  pulledData: boolean = false;

  constructor(private titleService: Title, private playlistInsightsService: PlaylistInsightsService) {}
  response: PlaylistInsightsResponse | undefined;

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Playlist Insights');
  }

  track(index: number): number {
    return index;
  }
  sendLink() {
    // @ts-ignore
    const url: String = this.urlForm.get('name').value;
    this.playlistInsightsService.sendURL(url).subscribe(value => (this.response = value));
    this.pulledData = true;
    console.log(this.response?.anomalousSong?.songTitle);
  }
}
