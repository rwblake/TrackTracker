import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GdprPolicyRoutingModule } from './gdpr-policy-routing.module';
import { GdprPolicyComponent } from './gdpr-policy.component';

@NgModule({
  declarations: [GdprPolicyComponent],
  imports: [CommonModule, GdprPolicyRoutingModule],
})
export class GdprPolicyModule {}
