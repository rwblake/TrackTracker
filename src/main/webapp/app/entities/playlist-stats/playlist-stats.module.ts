import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PlaylistStatsComponent } from './list/playlist-stats.component';
import { PlaylistStatsDetailComponent } from './detail/playlist-stats-detail.component';
import { PlaylistStatsUpdateComponent } from './update/playlist-stats-update.component';
import { PlaylistStatsDeleteDialogComponent } from './delete/playlist-stats-delete-dialog.component';
import { PlaylistStatsRoutingModule } from './route/playlist-stats-routing.module';

@NgModule({
  imports: [SharedModule, PlaylistStatsRoutingModule],
  declarations: [PlaylistStatsComponent, PlaylistStatsDetailComponent, PlaylistStatsUpdateComponent, PlaylistStatsDeleteDialogComponent],
})
export class PlaylistStatsModule {}
