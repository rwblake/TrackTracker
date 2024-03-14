import { Component, Input, OnInit } from '@angular/core';
import { Card } from '../../account/account_combined.model';

@Component({
  selector: 'jhi-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent implements OnInit {
  constructor() {}

  @Input() card!: Card;

  ngOnInit(): void {}
}
