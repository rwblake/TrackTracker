import { Component, Input, OnInit } from '@angular/core';

export interface LeaderboardCardData {
  title?: string;
  icon?: string;
  entries: LeaderboardCardEntry[];
}

export interface LeaderboardCardEntry {
  imageUrl?: string;
  name: string;
  value: string;
}

@Component({
  selector: 'jhi-leaderboard-card',
  templateUrl: './leaderboard-card.component.html',
  styleUrls: ['./leaderboard-card.component.scss'],
})
export class LeaderboardCardComponent implements OnInit {
  @Input() data: LeaderboardCardData | undefined;

  constructor() {}

  ngOnInit(): void {}
}
