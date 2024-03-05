import { Component, NgModule, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { PlaylistInsightsService } from './playlist-insights.service';
import { ArtistsToProportionResponse, PlaylistInsightsResponse, YearsToSongResponse } from './playlist-insights-response-interface';
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

  artistsPieChartTitle: string = 'Number of Songs Artist Appears On';
  artistsPieChart: { name: string; value: number }[] = [];

  xaxisLabelBar = 'Decade';
  yaxisLabelBar = 'Number of Songs';
  below = LegendPosition.Below;
  timePeriodBar: { name: string; value: number }[] = [];

  pulledData: boolean = false;

  constructor(private titleService: Title, private playlistInsightsService: PlaylistInsightsService) {}
  response: PlaylistInsightsResponse | undefined;

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Playlist Insights');
  }

  sendLink() {
    // @ts-ignore
    const url: String = this.urlForm.get('name').value;
    this.playlistInsightsService.sendURL(url).subscribe(value => this.onSuccessfulResponse(value));
  }

  onSuccessfulResponse(val: PlaylistInsightsResponse) {
    this.response = val;

    this.addArtistsToChart(this.response.artistsToProportions);
    this.addSongYearsToChart(this.response.yearsToSongs);
    this.pulledData = true;
  }

  addArtistsToChart(artistsToProportions: ArtistsToProportionResponse[]) {
    for (let i = 0; i < artistsToProportions.length; i++) {
      this.artistsPieChart.push({
        name: artistsToProportions[i].artistName,
        value: artistsToProportions[i].occurencesInPlaylist,
      });
    }
  }

  addSongYearsToChart(yearsToSongs: YearsToSongResponse[]) {
    for (let i = 0; i < yearsToSongs.length; i++) {
      this.timePeriodBar.push({
        name: yearsToSongs[i].year,
        value: yearsToSongs[i].songCount,
      });
    }
  }
}
