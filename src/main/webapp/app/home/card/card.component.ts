import { Component, Input, OnInit } from '@angular/core';
import { FeedCard } from '../../account/account_combined.model';
import dayjs from 'dayjs/esm';
import relativeTime from 'dayjs/esm/plugin/relativeTime';

@Component({
  selector: 'jhi-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent implements OnInit {
  @Input() card!: FeedCard;

  ngOnInit(): void {}

  getTimeStamp() {
    if (this.card.timeGenerated === undefined) return 'NULL';
    dayjs.extend(relativeTime);
    return dayjs(this.card.timeGenerated).fromNow();
  }

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
      case 'new-playlist':
        return 'New playlist analysed';
    }
  }
}
