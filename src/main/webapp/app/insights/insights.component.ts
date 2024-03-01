import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';

@Component({
  selector: 'jhi-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss'],
})
export class InsightsComponent implements OnInit {
  constructor(private titleService: Title) {}
  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - My Insights');
  }
}
