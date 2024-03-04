import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GdprPolicyComponent } from './gdpr-policy.component';

const routes: Routes = [{ path: '', component: GdprPolicyComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GdprPolicyRoutingModule {}
