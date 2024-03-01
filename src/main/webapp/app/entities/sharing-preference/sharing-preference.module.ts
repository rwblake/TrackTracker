import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SharingPreferenceComponent } from './list/sharing-preference.component';
import { SharingPreferenceDetailComponent } from './detail/sharing-preference-detail.component';
import { SharingPreferenceUpdateComponent } from './update/sharing-preference-update.component';
import { SharingPreferenceDeleteDialogComponent } from './delete/sharing-preference-delete-dialog.component';
import { SharingPreferenceRoutingModule } from './route/sharing-preference-routing.module';

@NgModule({
  imports: [SharedModule, SharingPreferenceRoutingModule],
  declarations: [
    SharingPreferenceComponent,
    SharingPreferenceDetailComponent,
    SharingPreferenceUpdateComponent,
    SharingPreferenceDeleteDialogComponent,
  ],
})
export class SharingPreferenceModule {}
