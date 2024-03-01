import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StreamComponent } from './list/stream.component';
import { StreamDetailComponent } from './detail/stream-detail.component';
import { StreamUpdateComponent } from './update/stream-update.component';
import { StreamDeleteDialogComponent } from './delete/stream-delete-dialog.component';
import { StreamRoutingModule } from './route/stream-routing.module';

@NgModule({
  imports: [SharedModule, StreamRoutingModule],
  declarations: [StreamComponent, StreamDetailComponent, StreamUpdateComponent, StreamDeleteDialogComponent],
})
export class StreamModule {}
