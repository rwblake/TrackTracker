import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICardMetric } from '../card-metric.model';

@Component({
  selector: 'jhi-card-metric-detail',
  templateUrl: './card-metric-detail.component.html',
})
export class CardMetricDetailComponent implements OnInit {
  cardMetric: ICardMetric | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cardMetric }) => {
      this.cardMetric = cardMetric;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
