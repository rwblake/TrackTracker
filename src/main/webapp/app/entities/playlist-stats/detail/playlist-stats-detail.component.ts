import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlaylistStats } from '../playlist-stats.model';

@Component({
  selector: 'jhi-playlist-stats-detail',
  templateUrl: './playlist-stats-detail.component.html',
})
export class PlaylistStatsDetailComponent implements OnInit {
  playlistStats: IPlaylistStats | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playlistStats }) => {
      this.playlistStats = playlistStats;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
