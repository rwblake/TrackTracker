import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlaylistStats } from '../playlist-stats.model';
import { PlaylistStatsService } from '../service/playlist-stats.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './playlist-stats-delete-dialog.component.html',
})
export class PlaylistStatsDeleteDialogComponent {
  playlistStats?: IPlaylistStats;

  constructor(protected playlistStatsService: PlaylistStatsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.playlistStatsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
