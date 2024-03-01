import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FeedComponent } from './list/feed.component';
import { FeedDetailComponent } from './detail/feed-detail.component';
import { FeedUpdateComponent } from './update/feed-update.component';
import { FeedDeleteDialogComponent } from './delete/feed-delete-dialog.component';
import { FeedRoutingModule } from './route/feed-routing.module';

@NgModule({
  imports: [SharedModule, FeedRoutingModule],
  declarations: [FeedComponent, FeedDetailComponent, FeedUpdateComponent, FeedDeleteDialogComponent],
})
export class FeedModule {}
