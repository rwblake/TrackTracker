import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

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
  @Input() timePeriodSelections: TimePeriod[] = [
    { periodDays: 7, label: 'Week' },
    { periodDays: 28, label: '4 Weeks' },
    { periodDays: 365, label: 'Year' },
    { periodDays: -1, label: 'All Time' },
  ];

  selectedTimePeriod!: TimePeriod;
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
    this.selectedTimePeriod = this.timePeriodSelections[0];
    this.setTimePeriod(this.selectedTimePeriod);
  }
}
