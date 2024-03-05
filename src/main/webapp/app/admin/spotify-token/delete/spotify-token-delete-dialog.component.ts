import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISpotifyToken } from '../spotify-token.model';
import { SpotifyTokenService } from '../service/spotify-token.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './spotify-token-delete-dialog.component.html',
})
export class SpotifyTokenDeleteDialogComponent {
  spotifyToken?: ISpotifyToken;

  constructor(protected spotifyTokenService: SpotifyTokenService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.spotifyTokenService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
