import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { SharingPreferenceFormService, SharingPreferenceFormGroup } from './sharing-preference-form.service';
import { ISharingPreference } from '../sharing-preference.model';
import { SharingPreferenceService } from '../service/sharing-preference.service';
import { IUserPreferences } from 'app/entities/user-preferences/user-preferences.model';
import { UserPreferencesService } from 'app/entities/user-preferences/service/user-preferences.service';
import { CardType } from 'app/entities/enumerations/card-type.model';
import { VisibilityPreference } from 'app/entities/enumerations/visibility-preference.model';

@Component({
  selector: 'jhi-sharing-preference-update',
  templateUrl: './sharing-preference-update.component.html',
})
export class SharingPreferenceUpdateComponent implements OnInit {
  isSaving = false;
  sharingPreference: ISharingPreference | null = null;
  cardTypeValues = Object.keys(CardType);
  visibilityPreferenceValues = Object.keys(VisibilityPreference);

  userPreferencesSharedCollection: IUserPreferences[] = [];

  editForm: SharingPreferenceFormGroup = this.sharingPreferenceFormService.createSharingPreferenceFormGroup();

  constructor(
    protected sharingPreferenceService: SharingPreferenceService,
    protected sharingPreferenceFormService: SharingPreferenceFormService,
    protected userPreferencesService: UserPreferencesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUserPreferences = (o1: IUserPreferences | null, o2: IUserPreferences | null): boolean =>
    this.userPreferencesService.compareUserPreferences(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sharingPreference }) => {
      this.sharingPreference = sharingPreference;
      if (sharingPreference) {
        this.updateForm(sharingPreference);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sharingPreference = this.sharingPreferenceFormService.getSharingPreference(this.editForm);
    if (sharingPreference.id !== null) {
      this.subscribeToSaveResponse(this.sharingPreferenceService.update(sharingPreference));
    } else {
      this.subscribeToSaveResponse(this.sharingPreferenceService.create(sharingPreference));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISharingPreference>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(sharingPreference: ISharingPreference): void {
    this.sharingPreference = sharingPreference;
    this.sharingPreferenceFormService.resetForm(this.editForm, sharingPreference);

    this.userPreferencesSharedCollection = this.userPreferencesService.addUserPreferencesToCollectionIfMissing<IUserPreferences>(
      this.userPreferencesSharedCollection,
      sharingPreference.appUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userPreferencesService
      .query()
      .pipe(map((res: HttpResponse<IUserPreferences[]>) => res.body ?? []))
      .pipe(
        map((userPreferences: IUserPreferences[]) =>
          this.userPreferencesService.addUserPreferencesToCollectionIfMissing<IUserPreferences>(
            userPreferences,
            this.sharingPreference?.appUser
          )
        )
      )
      .subscribe((userPreferences: IUserPreferences[]) => (this.userPreferencesSharedCollection = userPreferences));
  }
}
