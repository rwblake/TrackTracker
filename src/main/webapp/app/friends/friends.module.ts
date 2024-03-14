import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FriendsRoutingModule } from './friends-routing.module';
import { FriendsComponent } from './friends.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [FriendsComponent],
  imports: [CommonModule, FriendsRoutingModule, ReactiveFormsModule],
})
export class FriendsModule {}
