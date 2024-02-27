import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFriendRequest } from '../friend-request.model';
import { FriendRequestService } from '../service/friend-request.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './friend-request-delete-dialog.component.html',
})
export class FriendRequestDeleteDialogComponent {
  friendRequest?: IFriendRequest;

  constructor(protected friendRequestService: FriendRequestService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.friendRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
