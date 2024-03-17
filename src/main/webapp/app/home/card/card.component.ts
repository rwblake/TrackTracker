import { Component, Input, OnInit } from '@angular/core';
import { FeedCard } from '../../account/account_combined.model';

@Component({
  selector: 'jhi-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent implements OnInit {
  @Input() card!: FeedCard;

  ngOnInit(): void {}

  formatInferredCardType() {
    switch (this.card.inferredType) {
      case 'milestone':
        return 'Milestone';
      case 'personal':
        return 'Personal Insight';
      case 'friend-update':
        return 'Friend Update';
      case 'friend-request':
        return 'Friend request';
      case 'new-friend':
        return 'New friend';
    }
  }
}
