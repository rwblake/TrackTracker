import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CardTemplateFormService, CardTemplateFormGroup } from './card-template-form.service';
import { ICardTemplate } from '../card-template.model';
import { CardTemplateService } from '../service/card-template.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { Color } from 'app/entities/enumerations/color.model';
import { Layout } from 'app/entities/enumerations/layout.model';
import { Font } from 'app/entities/enumerations/font.model';

@Component({
  selector: 'jhi-card-template-update',
  templateUrl: './card-template-update.component.html',
})
export class CardTemplateUpdateComponent implements OnInit {
  isSaving = false;
  cardTemplate: ICardTemplate | null = null;
  colorValues = Object.keys(Color);
  layoutValues = Object.keys(Layout);
  fontValues = Object.keys(Font);

  appUsersSharedCollection: IAppUser[] = [];

  editForm: CardTemplateFormGroup = this.cardTemplateFormService.createCardTemplateFormGroup();

  constructor(
    protected cardTemplateService: CardTemplateService,
    protected cardTemplateFormService: CardTemplateFormService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cardTemplate }) => {
      this.cardTemplate = cardTemplate;
      if (cardTemplate) {
        this.updateForm(cardTemplate);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cardTemplate = this.cardTemplateFormService.getCardTemplate(this.editForm);
    if (cardTemplate.id !== null) {
      this.subscribeToSaveResponse(this.cardTemplateService.update(cardTemplate));
    } else {
      this.subscribeToSaveResponse(this.cardTemplateService.create(cardTemplate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICardTemplate>>): void {
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

  protected updateForm(cardTemplate: ICardTemplate): void {
    this.cardTemplate = cardTemplate;
    this.cardTemplateFormService.resetForm(this.editForm, cardTemplate);

    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      cardTemplate.appUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.cardTemplate?.appUser))
      )
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }
}
