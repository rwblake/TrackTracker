import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CardFormService, CardFormGroup } from './card-form.service';
import { ICard } from '../card.model';
import { CardService } from '../service/card.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { CardType } from 'app/entities/enumerations/card-type.model';

@Component({
  selector: 'jhi-card-update',
  templateUrl: './card-update.component.html',
})
export class CardUpdateComponent implements OnInit {
  isSaving = false;
  card: ICard | null = null;
  cardTypeValues = Object.keys(CardType);

  appUsersSharedCollection: IAppUser[] = [];

  editForm: CardFormGroup = this.cardFormService.createCardFormGroup();

  constructor(
    protected cardService: CardService,
    protected cardFormService: CardFormService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ card }) => {
      this.card = card;
      if (card) {
        this.updateForm(card);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const card = this.cardFormService.getCard(this.editForm);
    if (card.id !== null) {
      this.subscribeToSaveResponse(this.cardService.update(card));
    } else {
      this.subscribeToSaveResponse(this.cardService.create(card));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICard>>): void {
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

  protected updateForm(card: ICard): void {
    this.card = card;
    this.cardFormService.resetForm(this.editForm, card);

    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      card.appUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(map((appUsers: IAppUser[]) => this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.card?.appUser)))
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }
}
