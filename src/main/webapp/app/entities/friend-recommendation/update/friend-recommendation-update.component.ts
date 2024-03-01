import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FriendRecommendationFormService, FriendRecommendationFormGroup } from './friend-recommendation-form.service';
import { IFriendRecommendation } from '../friend-recommendation.model';
import { FriendRecommendationService } from '../service/friend-recommendation.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-friend-recommendation-update',
  templateUrl: './friend-recommendation-update.component.html',
})
export class FriendRecommendationUpdateComponent implements OnInit {
  isSaving = false;
  friendRecommendation: IFriendRecommendation | null = null;

  appUsersSharedCollection: IAppUser[] = [];
  aboutAppUsersCollection: IAppUser[] = [];

  editForm: FriendRecommendationFormGroup = this.friendRecommendationFormService.createFriendRecommendationFormGroup();

  constructor(
    protected friendRecommendationService: FriendRecommendationService,
    protected friendRecommendationFormService: FriendRecommendationFormService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ friendRecommendation }) => {
      this.friendRecommendation = friendRecommendation;
      if (friendRecommendation) {
        this.updateForm(friendRecommendation);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const friendRecommendation = this.friendRecommendationFormService.getFriendRecommendation(this.editForm);
    if (friendRecommendation.id !== null) {
      this.subscribeToSaveResponse(this.friendRecommendationService.update(friendRecommendation));
    } else {
      this.subscribeToSaveResponse(this.friendRecommendationService.create(friendRecommendation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFriendRecommendation>>): void {
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

  protected updateForm(friendRecommendation: IFriendRecommendation): void {
    this.friendRecommendation = friendRecommendation;
    this.friendRecommendationFormService.resetForm(this.editForm, friendRecommendation);

    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      friendRecommendation.forAppUser
    );
    this.aboutAppUsersCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.aboutAppUsersCollection,
      friendRecommendation.aboutAppUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.friendRecommendation?.forAppUser)
        )
      )
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));

    this.appUserService
      .query({ filter: 'aboutfriendrecommendation-is-null' })
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.friendRecommendation?.aboutAppUser)
        )
      )
      .subscribe((appUsers: IAppUser[]) => (this.aboutAppUsersCollection = appUsers));
  }
}
