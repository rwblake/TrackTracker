import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFeedCard } from '../feed-card.model';
import { FeedCardService } from '../service/feed-card.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './feed-card-delete-dialog.component.html',
})
export class FeedCardDeleteDialogComponent {
  feedCard?: IFeedCard;

  constructor(protected feedCardService: FeedCardService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.feedCardService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
