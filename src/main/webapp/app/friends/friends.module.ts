import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FriendsRoutingModule } from './friends-routing.module';
import { FriendsComponent } from './friends.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [FriendsComponent],
  imports: [CommonModule, FriendsRoutingModule, ReactiveFormsModule, FontAwesomeModule, FormsModule],
})
export class FriendsModule {}
