import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICardTemplate } from '../card-template.model';

@Component({
  selector: 'jhi-card-template-detail',
  templateUrl: './card-template-detail.component.html',
})
export class CardTemplateDetailComponent implements OnInit {
  cardTemplate: ICardTemplate | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cardTemplate }) => {
      this.cardTemplate = cardTemplate;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
