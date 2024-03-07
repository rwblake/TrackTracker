import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { TimePeriod } from '../time-period-picker/time-period-picker.component';

@Component({
  selector: 'jhi-friends-insights',
  templateUrl: './friends-insights.component.html',
  styleUrls: ['./friends-insights.component.scss'],
})
export class FriendsInsightsComponent implements OnInit {
  selectedTimePeriod?: TimePeriod;

  constructor(private titleService: Title) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Friend Insights');
  }

  onTimePeriodChange(period: TimePeriod) {
    this.selectedTimePeriod = period;
  }
}
