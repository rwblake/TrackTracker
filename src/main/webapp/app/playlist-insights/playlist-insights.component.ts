import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';

@Component({
  selector: 'jhi-playlist-insights',
  templateUrl: './playlist-insights.component.html',
  styleUrls: ['./playlist-insights.component.scss'],
})
export class PlaylistInsightsComponent implements OnInit {
  constructor(private titleService: Title) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Playlist Insights');
  }

  sendLink() {}
}
