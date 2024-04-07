import { Component, Input, OnInit } from '@angular/core';
import { FeedCard } from '../../account/account_combined.model';
import dayjs from 'dayjs/esm';
import relativeTime from 'dayjs/esm/plugin/relativeTime';
import { Params, Router } from '@angular/router';

@Component({
  selector: 'jhi-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent implements OnInit {
  @Input() card!: FeedCard;

  ngOnInit(): void {}

  constructor(private router: Router) {}

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

  async openCard() {
    const url = decodeURIComponent(this.card.href.toString());

    const params: Params = {};

    // Splitting the URL to separate the path and query parameters
    const [path, queryString] = url.split('?');

    if (queryString) {
      // Splitting the query string to get individual parameters
      const queryParams = queryString.split('&');

      // Iterating over each parameter and adding it to the params object
      queryParams.forEach(param => {
        const [key, value] = param.split('=');
        params[key] = value;
      });
    }

    await this.router.navigate([path], { queryParams: params });
  }
}
