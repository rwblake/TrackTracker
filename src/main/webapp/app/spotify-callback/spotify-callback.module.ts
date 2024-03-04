import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SpotifyCallbackRoutingModule } from './spotify-callback-routing.module';
import { SpotifyCallbackComponent } from './spotify-callback.component';

@NgModule({
  declarations: [SpotifyCallbackComponent],
  imports: [CommonModule, SpotifyCallbackRoutingModule],
})
export class SpotifyCallbackModule {}
