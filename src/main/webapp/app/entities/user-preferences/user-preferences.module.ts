import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserPreferencesComponent } from './list/user-preferences.component';
import { UserPreferencesDetailComponent } from './detail/user-preferences-detail.component';
import { UserPreferencesUpdateComponent } from './update/user-preferences-update.component';
import { UserPreferencesDeleteDialogComponent } from './delete/user-preferences-delete-dialog.component';
import { UserPreferencesRoutingModule } from './route/user-preferences-routing.module';

@NgModule({
  imports: [SharedModule, UserPreferencesRoutingModule],
  declarations: [
    UserPreferencesComponent,
    UserPreferencesDetailComponent,
    UserPreferencesUpdateComponent,
    UserPreferencesDeleteDialogComponent,
  ],
})
export class UserPreferencesModule {}
