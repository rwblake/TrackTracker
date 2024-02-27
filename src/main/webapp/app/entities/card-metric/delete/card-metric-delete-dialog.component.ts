import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICardMetric } from '../card-metric.model';
import { CardMetricService } from '../service/card-metric.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './card-metric-delete-dialog.component.html',
})
export class CardMetricDeleteDialogComponent {
  cardMetric?: ICardMetric;

  constructor(protected cardMetricService: CardMetricService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cardMetricService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
