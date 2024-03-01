import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICardTemplate } from '../card-template.model';
import { CardTemplateService } from '../service/card-template.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './card-template-delete-dialog.component.html',
})
export class CardTemplateDeleteDialogComponent {
  cardTemplate?: ICardTemplate;

  constructor(protected cardTemplateService: CardTemplateService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cardTemplateService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
