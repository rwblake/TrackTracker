import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PlaylistComponent } from './list/playlist.component';
import { PlaylistDetailComponent } from './detail/playlist-detail.component';
import { PlaylistUpdateComponent } from './update/playlist-update.component';
import { PlaylistDeleteDialogComponent } from './delete/playlist-delete-dialog.component';
import { PlaylistRoutingModule } from './route/playlist-routing.module';

@NgModule({
  imports: [SharedModule, PlaylistRoutingModule],
  declarations: [PlaylistComponent, PlaylistDetailComponent, PlaylistUpdateComponent, PlaylistDeleteDialogComponent],
})
export class PlaylistModule {}
