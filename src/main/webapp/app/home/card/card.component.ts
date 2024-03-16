import { Component, Input, OnInit } from '@angular/core';
import { Card } from '../../account/account_combined.model';
import { CardType } from '../../entities/enumerations/card-type.model';

@Component({
  selector: 'jhi-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent implements OnInit {
  @Input() card!: Card;

  ngOnInit(): void {}

  // generates the message to show on the card
  generateMessage(): string {
    switch (this.card.inferredType) {
      case 'milestone': {
        switch (this.card.metric) {
          case CardType.LISTENING_DURATION:
            return `You have spent ${this.card.metricValue} minutes listening to Spotify!`;
          case CardType.NO_OF_SONGS_LISTENED:
            return `You have listened to ${this.card.metricValue} songs!`;
          case CardType.NO_OF_FRIENDS:
            return `You now have ${this.card.metricValue} friends!`;
          case CardType.TOP_ARTIST:
            return `Your top genre of all time is (artistID: ${this.card.metricValue})!`;
          default:
            return 'Not yet implemented';
        }
      }

      case 'personal': {
        return 'Personal Insight';
      }

      case 'friend-update': {
        return 'Friend Update';
      }

      case 'friend-request': {
        return 'Friend request';
      }

      case 'new-friend': {
        return 'New friend';
      }
    }
  }

  formatInferredCardType(): string {
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

  // decides which icon to show
  generateIcon(): string {
    return 'music_note';
  }
}
