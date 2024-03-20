import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { CardComponent } from './card/card.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE]), BrowserAnimationsModule],
  declarations: [HomeComponent, CardComponent],
})
export class HomeModule {}
