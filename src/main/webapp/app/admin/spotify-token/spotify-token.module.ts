import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SpotifyTokenComponent } from './list/spotify-token.component';
import { SpotifyTokenDetailComponent } from './detail/spotify-token-detail.component';
import { SpotifyTokenUpdateComponent } from './update/spotify-token-update.component';
import { SpotifyTokenDeleteDialogComponent } from './delete/spotify-token-delete-dialog.component';
import { SpotifyTokenRoutingModule } from './route/spotify-token-routing.module';

@NgModule({
  imports: [SharedModule, SpotifyTokenRoutingModule],
  declarations: [SpotifyTokenComponent, SpotifyTokenDetailComponent, SpotifyTokenUpdateComponent, SpotifyTokenDeleteDialogComponent],
})
export class SpotifyTokenModule {}
