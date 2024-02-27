import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFriendRequest } from '../friend-request.model';

@Component({
  selector: 'jhi-friend-request-detail',
  templateUrl: './friend-request-detail.component.html',
})
export class FriendRequestDetailComponent implements OnInit {
  friendRequest: IFriendRequest | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ friendRequest }) => {
      this.friendRequest = friendRequest;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
