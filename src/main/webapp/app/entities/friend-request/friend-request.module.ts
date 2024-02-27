import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FriendRequestComponent } from './list/friend-request.component';
import { FriendRequestDetailComponent } from './detail/friend-request-detail.component';
import { FriendRequestUpdateComponent } from './update/friend-request-update.component';
import { FriendRequestDeleteDialogComponent } from './delete/friend-request-delete-dialog.component';
import { FriendRequestRoutingModule } from './route/friend-request-routing.module';

@NgModule({
  imports: [SharedModule, FriendRequestRoutingModule],
  declarations: [FriendRequestComponent, FriendRequestDetailComponent, FriendRequestUpdateComponent, FriendRequestDeleteDialogComponent],
})
export class FriendRequestModule {}
