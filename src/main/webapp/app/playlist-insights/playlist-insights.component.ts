import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { PlaylistInsightsService } from './playlist-insights.service';

@Component({
  selector: 'jhi-playlist-insights',
  templateUrl: './playlist-insights.component.html',
  styleUrls: ['./playlist-insights.component.scss'],
})
export class PlaylistInsightsComponent implements OnInit {
  urlForm: FormGroup = new FormGroup({ name: new FormControl('', [Validators.required]) });
  constructor(private titleService: Title, private playlistInsightsService: PlaylistInsightsService) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Playlist Insights');
  }

  sendLink() {
    // @ts-ignore
    const url: String = this.urlForm.get('name').value;
    this.playlistInsightsService.sendURL(url).subscribe(value => console.log(value));
  }
}
