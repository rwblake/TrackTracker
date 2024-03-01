import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISharingPreference } from '../sharing-preference.model';

@Component({
  selector: 'jhi-sharing-preference-detail',
  templateUrl: './sharing-preference-detail.component.html',
})
export class SharingPreferenceDetailComponent implements OnInit {
  sharingPreference: ISharingPreference | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sharingPreference }) => {
      this.sharingPreference = sharingPreference;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
