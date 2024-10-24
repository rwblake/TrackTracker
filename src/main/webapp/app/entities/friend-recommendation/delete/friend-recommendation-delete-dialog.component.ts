import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFriendRecommendation } from '../friend-recommendation.model';
import { FriendRecommendationService } from '../service/friend-recommendation.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './friend-recommendation-delete-dialog.component.html',
})
export class FriendRecommendationDeleteDialogComponent {
  friendRecommendation?: IFriendRecommendation;

  constructor(protected friendRecommendationService: FriendRecommendationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.friendRecommendationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
