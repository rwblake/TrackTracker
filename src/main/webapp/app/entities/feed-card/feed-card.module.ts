import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FeedCardComponent } from './list/feed-card.component';
import { FeedCardDetailComponent } from './detail/feed-card-detail.component';
import { FeedCardUpdateComponent } from './update/feed-card-update.component';
import { FeedCardDeleteDialogComponent } from './delete/feed-card-delete-dialog.component';
import { FeedCardRoutingModule } from './route/feed-card-routing.module';

@NgModule({
  imports: [SharedModule, FeedCardRoutingModule],
  declarations: [FeedCardComponent, FeedCardDetailComponent, FeedCardUpdateComponent, FeedCardDeleteDialogComponent],
})
export class FeedCardModule {}
