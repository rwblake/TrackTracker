import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFeedCard } from '../feed-card.model';

@Component({
  selector: 'jhi-feed-card-detail',
  templateUrl: './feed-card-detail.component.html',
})
export class FeedCardDetailComponent implements OnInit {
  feedCard: IFeedCard | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ feedCard }) => {
      this.feedCard = feedCard;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
