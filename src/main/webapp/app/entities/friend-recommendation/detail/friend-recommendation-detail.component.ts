import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFriendRecommendation } from '../friend-recommendation.model';

@Component({
  selector: 'jhi-friend-recommendation-detail',
  templateUrl: './friend-recommendation-detail.component.html',
})
export class FriendRecommendationDetailComponent implements OnInit {
  friendRecommendation: IFriendRecommendation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ friendRecommendation }) => {
      this.friendRecommendation = friendRecommendation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
