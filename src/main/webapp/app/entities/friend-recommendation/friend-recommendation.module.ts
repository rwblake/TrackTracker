import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FriendRecommendationComponent } from './list/friend-recommendation.component';
import { FriendRecommendationDetailComponent } from './detail/friend-recommendation-detail.component';
import { FriendRecommendationUpdateComponent } from './update/friend-recommendation-update.component';
import { FriendRecommendationDeleteDialogComponent } from './delete/friend-recommendation-delete-dialog.component';
import { FriendRecommendationRoutingModule } from './route/friend-recommendation-routing.module';

@NgModule({
  imports: [SharedModule, FriendRecommendationRoutingModule],
  declarations: [
    FriendRecommendationComponent,
    FriendRecommendationDetailComponent,
    FriendRecommendationUpdateComponent,
    FriendRecommendationDeleteDialogComponent,
  ],
})
export class FriendRecommendationModule {}
