import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SpotifyCallbackComponent } from './spotify-callback.component';

const routes: Routes = [{ path: '', component: SpotifyCallbackComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SpotifyCallbackRoutingModule {}
