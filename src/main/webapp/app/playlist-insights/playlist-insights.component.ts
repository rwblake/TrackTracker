import { Component, NgModule, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { PlaylistInsightsService } from './playlist-insights.service';
import {
  ArtistMapResponse,
  PlaylistInsightsResponse,
  YearMapResponse,
  GenreMapResponse,
  GraphDataResponse,
} from './playlist-insights-response-interface';
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

  //  Max number of elements to show on a pie chart
  pieLimit: number = 10;

  // Stores pasted-in playlist input
  linkInput: string = '';

  // Colour scheme for the various charts
  chartScheme: Color = {
    name: 'pieSchemeA',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#7825BC', '#B237EC', '#EA3AC1', '#EA002B', '#FF3407', '#FF6528', '#FF8E16', '#FFB219', '#FFD54C', '#FFEFA3'],
  };

  // Information for the pie charts
  artistsPieChart: { name: string; value: number }[] = [];
  genrePieChart: { name: string; value: number }[] = [];

  // Data for the Time Period bar charts
  yearBarChart: { name: string; value: number }[] = [];
  decadeBarChart: { name: string; value: number }[] = [];

  // Used to check if data has been pulled
  pulledData: boolean = false;
  waitingForResponse: boolean = false;
  showWaitingMessage: boolean = false;
  showErrorMessage: boolean = false;

  valenceValue: number = 0;
  energyValue: number = 0;
  acousticnessValue: number = 0;
  danceabilityValue: number = 0;

  constructor(private titleService: Title, private playlistInsightsService: PlaylistInsightsService) {}
  response: PlaylistInsightsResponse | undefined;

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Playlist Insights');
  }

  // Sends playlist link to backend HTTP endpoint and subscribes to response
  async sendLink() {
    // @ts-ignore
    const url: String = this.urlForm.get('name').value;

    this.waitingForResponse = true;
    this.pulledData = false;
    this.showErrorMessage = false;

    this.playlistInsightsService.sendURL(url).subscribe({
      next: v => this.onSuccessfulResponse(v),
      error: e => this.onFailure(e),
    });

    // Pulling data is sometimes effectively instant. It looks bad if the "waiting" message pops up
    // for half a second - so we only show it if we've been waiting for some time.
    await this.delay(750);
    if (this.waitingForResponse) {
      this.showWaitingMessage = true;
    }
  }

  /** Wait for the specified number of seconds */
  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  onFailure(error: any) {
    this.waitingForResponse = false;
    this.showWaitingMessage = false;
    this.pulledData = false;
    this.showErrorMessage = true;
  }

  onSuccessfulResponse(val: PlaylistInsightsResponse) {
    this.waitingForResponse = false;
    this.showWaitingMessage = false;

    this.response = val;
    this.addArtistsToChart(this.response.graphData.artistMaps);
    this.addYearsToChart(this.response.graphData.yearMaps);
    this.addDecadesToChart(this.response.graphData.decadeMaps);
    this.addGenresToChart(this.response.graphData.genreMaps);

    this.valenceValue = this.response.averageValence * 100;
    this.energyValue = this.response.averageEnergy * 100;
    this.acousticnessValue = this.response.averageAcousticness * 100;
    this.danceabilityValue = this.response.averageDanceability * 100;

    // Reveal the lower section of the page.
    this.pulledData = true;
  }

  addArtistsToChart(artistsToProportions: ArtistMapResponse[]) {
    this.artistsPieChart = [];

    for (let i = artistsToProportions.length - 1; i >= 0; i--) {
      if (i < artistsToProportions.length - this.pieLimit) {
        break;
      }
      this.artistsPieChart.push({
        name: artistsToProportions[i].artistName,
        value: artistsToProportions[i].occurrencesInPlaylist,
      });
    }
  }

  addGenresToChart(genresToCounts: GenreMapResponse[]) {
    this.genrePieChart = [];

    for (let i = genresToCounts.length - 1; i >= 0; i--) {
      if (i < genresToCounts.length - this.pieLimit) {
        break;
      }
      this.genrePieChart.push({
        name: genresToCounts[i].genreName,
        value: genresToCounts[i].occurrencesInPlaylist,
      });
    }
  }

  addYearsToChart(yearsToSongs: YearMapResponse[]) {
    this.yearBarChart = [];

    for (let i = 0; i < yearsToSongs.length; i++) {
      this.yearBarChart.push({
        name: yearsToSongs[i].year,
        value: yearsToSongs[i].songCount,
      });
    }
  }

  addDecadesToChart(decadesToSongs: YearMapResponse[]) {
    this.decadeBarChart = [];

    for (let i = 0; i < decadesToSongs.length; i++) {
      this.decadeBarChart.push({
        name: decadesToSongs[i].year,
        value: decadesToSongs[i].songCount,
      });
    }
  }

  protected readonly right = right;
}
