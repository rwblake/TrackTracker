import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { PlaylistInsightsService } from './playlist-insights.service';
import { ArtistMapResponse, PlaylistInsightsResponse, YearMapResponse, GenreMapResponse } from './playlist-insights-response-interface';
import { Color, ScaleType } from '@swimlane/ngx-charts';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';
import { IPlaylist } from '../entities/playlist/playlist.model';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

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

  // User playlists
  recentPlaylists: IPlaylist[] = [];
  hasPlaylists: boolean = false;

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

  showByDecade: boolean = true;

  valenceValue: number = 0;
  energyValue: number = 0;
  acousticnessValue: number = 0;
  danceabilityValue: number = 0;

  playlistURLStart: string = 'https://open.spotify.com/playlist/';
  trackURLStart: string = 'https://open.spotify.com/track/';

  happiestSongURL: string = '';
  energeticSongURL: string = '';
  sumsUpSongURL: string = '';
  anomalousSongURL: string = '';

  constructor(
    private titleService: Title,
    private playlistInsightsService: PlaylistInsightsService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}
  response: PlaylistInsightsResponse | undefined;

  // Query parameters. A playlist may be passed in to load straight away.
  passedParams: any;
  passedPlaylist: string = '';

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Playlist Analyser');

    this.handleParameters(this.activatedRoute.snapshot.queryParams);

    // Pull recently viewed playlists for quick access
    this.playlistInsightsService.retrieveUserPlaylists().subscribe({
      next: v => this.onRecentlyViewedRetrievalSuccess(v),
    });
  }

  constructPlaylistLink(id: string | null | undefined): string {
    // This will actually never happen.
    if (id === undefined || id === null) {
      return '';
    }

    // Gets the current page (without parameters) and attaches the playlist ID.
    return this.router.url.split('?')[0] + '?playlistID=' + id;
  }

  handleParameters(val: any): void {
    this.passedParams = val;

    // If no playlist supplied, break and load the page as normal.
    if (val['playlistID'] === undefined) {
      return;
    }

    // If there is a playlist supplied, fetch its insights - or try to.
    this.passedPlaylist = val['playlistID'];
    this.sendPlaylistURL(this.playlistURLStart + this.passedPlaylist);
  }

  // Sends playlist link to backend HTTP endpoint and subscribes to response
  async sendLink() {
    // @ts-ignore
    const url: string = this.urlForm.get('name').value;
    await this.sendPlaylistURL(url);
  }

  async sendPlaylistURL(URL: string) {
    this.waitingForResponse = true;
    this.pulledData = false;
    this.showErrorMessage = false;

    this.playlistInsightsService.sendURL(URL).subscribe({
      next: v => this.onPlaylistRetrievalSuccess(v),
      error: e => this.onPlaylistRetrievalFailure(e),
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

  onPlaylistRetrievalFailure(error: any) {
    this.waitingForResponse = false;
    this.showWaitingMessage = false;
    this.pulledData = false;
    this.showErrorMessage = true;
  }

  onPlaylistRetrievalSuccess(val: PlaylistInsightsResponse) {
    this.waitingForResponse = false;
    this.showWaitingMessage = false;

    this.response = val;

    this.setCardURLs(this.response);
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

  onRecentlyViewedRetrievalSuccess(val: IPlaylist[]) {
    this.recentPlaylists = val;
    this.hasPlaylists = this.recentPlaylists.length != 0;
  }

  setCardURLs(response: PlaylistInsightsResponse) {
    this.happiestSongURL = this.trackURLStart + response.happiestSong.songSpotifyID;
    this.energeticSongURL = this.trackURLStart + response.energeticSong.songSpotifyID;
    this.sumsUpSongURL = this.trackURLStart + response.sumsUpSong.songSpotifyID;
    this.anomalousSongURL = this.trackURLStart + response.anomalousSong.songSpotifyID;
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

  onTimeFormatChange(period: TimePeriod) {
    this.showByDecade = period.label !== 'Year';
  }
}
