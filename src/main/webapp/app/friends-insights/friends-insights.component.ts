import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';

@Component({
  selector: 'jhi-friends-insights',
  templateUrl: './friends-insights.component.html',
  styleUrls: ['./friends-insights.component.scss'],
})
export class FriendsInsightsComponent implements OnInit {
  constructor(private titleService: Title) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Friend Insights');
  }
}
