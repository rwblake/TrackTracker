import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CardTemplateComponent } from './list/card-template.component';
import { CardTemplateDetailComponent } from './detail/card-template-detail.component';
import { CardTemplateUpdateComponent } from './update/card-template-update.component';
import { CardTemplateDeleteDialogComponent } from './delete/card-template-delete-dialog.component';
import { CardTemplateRoutingModule } from './route/card-template-routing.module';

@NgModule({
  imports: [SharedModule, CardTemplateRoutingModule],
  declarations: [CardTemplateComponent, CardTemplateDetailComponent, CardTemplateUpdateComponent, CardTemplateDeleteDialogComponent],
})
export class CardTemplateModule {}
