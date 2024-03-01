import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserPreferences } from '../user-preferences.model';

@Component({
  selector: 'jhi-user-preferences-detail',
  templateUrl: './user-preferences-detail.component.html',
})
export class UserPreferencesDetailComponent implements OnInit {
  userPreferences: IUserPreferences | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userPreferences }) => {
      this.userPreferences = userPreferences;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
