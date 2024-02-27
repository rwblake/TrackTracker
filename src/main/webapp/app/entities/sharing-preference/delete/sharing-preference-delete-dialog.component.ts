import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISharingPreference } from '../sharing-preference.model';
import { SharingPreferenceService } from '../service/sharing-preference.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './sharing-preference-delete-dialog.component.html',
})
export class SharingPreferenceDeleteDialogComponent {
  sharingPreference?: ISharingPreference;

  constructor(protected sharingPreferenceService: SharingPreferenceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sharingPreferenceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
