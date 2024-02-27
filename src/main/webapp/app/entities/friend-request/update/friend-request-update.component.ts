import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FriendRequestFormService, FriendRequestFormGroup } from './friend-request-form.service';
import { IFriendRequest } from '../friend-request.model';
import { FriendRequestService } from '../service/friend-request.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

@Component({
  selector: 'jhi-friend-request-update',
  templateUrl: './friend-request-update.component.html',
})
export class FriendRequestUpdateComponent implements OnInit {
  isSaving = false;
  friendRequest: IFriendRequest | null = null;

  appUsersSharedCollection: IAppUser[] = [];
  initiatingAppUsersCollection: IAppUser[] = [];

  editForm: FriendRequestFormGroup = this.friendRequestFormService.createFriendRequestFormGroup();

  constructor(
    protected friendRequestService: FriendRequestService,
    protected friendRequestFormService: FriendRequestFormService,
    protected appUserService: AppUserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareAppUser = (o1: IAppUser | null, o2: IAppUser | null): boolean => this.appUserService.compareAppUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ friendRequest }) => {
      this.friendRequest = friendRequest;
      if (friendRequest) {
        this.updateForm(friendRequest);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const friendRequest = this.friendRequestFormService.getFriendRequest(this.editForm);
    if (friendRequest.id !== null) {
      this.subscribeToSaveResponse(this.friendRequestService.update(friendRequest));
    } else {
      this.subscribeToSaveResponse(this.friendRequestService.create(friendRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFriendRequest>>): void {
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

  protected updateForm(friendRequest: IFriendRequest): void {
    this.friendRequest = friendRequest;
    this.friendRequestFormService.resetForm(this.editForm, friendRequest);

    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.appUsersSharedCollection,
      friendRequest.toAppUser
    );
    this.initiatingAppUsersCollection = this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(
      this.initiatingAppUsersCollection,
      friendRequest.initiatingAppUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.friendRequest?.toAppUser)
        )
      )
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));

    this.appUserService
      .query({ filter: 'intitiatingfriendrequest-is-null' })
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing<IAppUser>(appUsers, this.friendRequest?.initiatingAppUser)
        )
      )
      .subscribe((appUsers: IAppUser[]) => (this.initiatingAppUsersCollection = appUsers));
  }
}
