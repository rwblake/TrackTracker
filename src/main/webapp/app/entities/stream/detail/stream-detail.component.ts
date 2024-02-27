import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStream } from '../stream.model';

@Component({
  selector: 'jhi-stream-detail',
  templateUrl: './stream-detail.component.html',
})
export class StreamDetailComponent implements OnInit {
  stream: IStream | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stream }) => {
      this.stream = stream;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
