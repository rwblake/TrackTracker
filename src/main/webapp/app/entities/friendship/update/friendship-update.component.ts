import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FriendshipFormService, FriendshipFormGroup } from './friendship-form.service';
import { IFriendship } from '../friendship.model';
import { FriendshipService } from '../service/friendship.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-friendship-update',
  templateUrl: './friendship-update.component.html',
})
export class FriendshipUpdateComponent implements OnInit {
  isSaving = false;
  friendship: IFriendship | null = null;

  appUsersSharedCollection: IAppUser[] = [];

  editForm: FriendshipFormGroup = this.friendshipFormService.createFriendshipFormGroup();

  constructor(
    protected friendshipService: FriendshipService,
    protected friendshipFormService: FriendshipFormService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ friendship }) => {
      this.friendship = friendship;
      if (friendship) {
        this.updateForm(friendship);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const friendship = this.friendshipFormService.getFriendship(this.editForm);
    if (friendship.id !== null) {
      this.subscribeToSaveResponse(this.friendshipService.update(friendship));
    } else {
      this.subscribeToSaveResponse(this.friendshipService.create(friendship));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFriendship>>): void {
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

  protected updateForm(friendship: IFriendship): void {
    this.friendship = friendship;
    this.friendshipFormService.resetForm(this.editForm, friendship);

    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      friendship.friendInitiating,
      friendship.friendAccepting
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
            appUsers,
            this.friendship?.friendInitiating,
            this.friendship?.friendAccepting
          )
        )
      )
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));
  }
}
