import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISpotifyToken } from '../spotify-token.model';

@Component({
  selector: 'jhi-spotify-token-detail',
  templateUrl: './spotify-token-detail.component.html',
})
export class SpotifyTokenDetailComponent implements OnInit {
  spotifyToken: ISpotifyToken | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spotifyToken }) => {
      this.spotifyToken = spotifyToken;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
