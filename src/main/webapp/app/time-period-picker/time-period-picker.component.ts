import { Component, EventEmitter, OnInit, Output } from '@angular/core';

export interface TimePeriod {
  periodDays: number;
  label: string;
}

@Component({
  selector: 'jhi-time-period-picker',
  templateUrl: './time-period-picker.component.html',
  styleUrls: ['./time-period-picker.component.scss'],
})
export class TimePeriodPickerComponent implements OnInit {
  timePeriods: TimePeriod[] = [
    { periodDays: 7, label: 'Week' },
    { periodDays: 28, label: '4 Weeks' },
    { periodDays: 365, label: 'Year' },
    { periodDays: -1, label: 'All Time' },
  ];

  selectedTimePeriod: TimePeriod = this.timePeriods[0];
  @Output() timePeriodChangeEvent = new EventEmitter<TimePeriod>();

  isNavbarCollapsed = true;

  constructor() {}

  setTimePeriod(period: TimePeriod): void {
    this.selectedTimePeriod = period;
    this.timePeriodChangeEvent.emit(this.selectedTimePeriod);
    this.isNavbarCollapsed = true;
  }

  toggleNavbar(): void {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  ngOnInit(): void {
    this.setTimePeriod(this.selectedTimePeriod);
  }
}
