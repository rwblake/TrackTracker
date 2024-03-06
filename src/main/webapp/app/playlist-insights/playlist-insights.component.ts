import { Component, NgModule, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { PlaylistInsightsService } from './playlist-insights.service';
import { ArtistsToProportionResponse, PlaylistInsightsResponse, YearsToSongResponse } from './playlist-insights-response-interface';
import { InsightsComponent } from '../insights/insights.component';
import { CommonModule } from '@angular/common';
import { InsightsRoutingModule } from '../insights/insights-routing.module';
import { Color, LegendPosition, NgxChartsModule, ScaleType } from '@swimlane/ngx-charts';
import { SharedModule } from '../shared/shared.module';
import { right } from '@popperjs/core';

@Component({
  selector: 'jhi-playlist-insights',
  templateUrl: './playlist-insights.component.html',
  styleUrls: ['./playlist-insights.component.scss'],
})
export class PlaylistInsightsComponent implements OnInit {
  urlForm: FormGroup = new FormGroup({ name: new FormControl('', [Validators.required]) });

  // Stores pasted-in playlist input
  linkInput: string = '';

  // Colour schemes for the various charts

  pieSchemeA: Color = {
    name: 'pieSchemeA',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#7825BC', '#B237EC', '#EA3AC1', '#EA002B', '#FF3407', '#FF6528', '#FF8E16', '#FFB219', '#FFD54C', '#FFEFA3'],
  };
  barScheme: Color = {
    name: 'barScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#BF31A8', '#B237EC', '#D62839', '#E66637', '#F49D37'],
  };
  valenceScheme: Color = {
    name: 'valenceScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#B237EC'],
  };
  energyScheme: Color = {
    name: 'energyScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#EA002B'],
  };
  acousticnessScheme: Color = {
    name: 'acousticnessScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#FF6528'],
  };

  danceabilityScheme: Color = {
    name: 'danceabilityScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#FFD54C'],
  };

  cardImageSize: number = 150;

  // Information for the pie chart
  artistsPieChartTitle: string = 'Number of Songs Artist Appears On';
  artistsPieChart: { name: string; value: number }[] = [];

  // Information for the bar chart
  xaxisLabelBar = 'Decade';
  yaxisLabelBar = 'Number of Songs';
  timePeriodBar: { name: string; value: number }[] = [];

  // Used to check if data has been pulled
  pulledData: boolean = false;

  valenceValue: number = 0;
  energyValue: number = 0;
  acousticnessValue: number = 0;
  danceabilityValue: number = 0;

  averageBarsHeight: number = 25;
  averageBarsWidth: number = 500;

  constructor(private titleService: Title, private playlistInsightsService: PlaylistInsightsService) {}
  response: PlaylistInsightsResponse | undefined;

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Playlist Insights');
  }

  sendLink() {
    // @ts-ignore
    const url: String = this.urlForm.get('name').value;
    this.playlistInsightsService.sendURL(url).subscribe(
      value => this.onSuccessfulResponse(value),
      error => this.onFailure()
    );
  }

  onFailure() {
    this.pulledData = false;
  }
  onSuccessfulResponse(val: PlaylistInsightsResponse) {
    this.response = val;
    this.addArtistsToChart(this.response.artistsToProportions);
    this.addSongYearsToChart(this.response.yearsToSongs);
    this.valenceValue = this.response.averageValence;
    this.energyValue = this.response.averageEnergy;
    this.acousticnessValue = this.response.averageAcousticness;
    this.danceabilityValue = this.response.averageDanceability;

    // reveal the charts!
    this.pulledData = true;
  }

  addArtistsToChart(artistsToProportions: ArtistsToProportionResponse[]) {
    this.artistsPieChart = [];

    for (let i = artistsToProportions.length - 1; i >= 0; i--) {
      if (i < artistsToProportions.length - 10) {
        break;
      }
      this.artistsPieChart.push({
        name: artistsToProportions[i].artistName,
        value: artistsToProportions[i].occurrencesInPlaylist,
      });
    }
  }

  addSongYearsToChart(yearsToSongs: YearsToSongResponse[]) {
    this.timePeriodBar = [];

    for (let i = 0; i < yearsToSongs.length; i++) {
      this.timePeriodBar.push({
        name: yearsToSongs[i].year,
        value: yearsToSongs[i].songCount,
      });
    }
  }

  protected readonly right = right;
  protected readonly LegendPosition = LegendPosition;
}
