import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISong } from '../song.model';

@Component({
  selector: 'jhi-song-detail',
  templateUrl: './song-detail.component.html',
})
export class SongDetailComponent implements OnInit {
  song: ISong | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ song }) => {
      this.song = song;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
