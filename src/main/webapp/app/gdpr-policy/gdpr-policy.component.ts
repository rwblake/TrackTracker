import { Component } from '@angular/core';

@Component({
  selector: 'jhi-gdpr-policy',
  templateUrl: './gdpr-policy.component.html',
  styleUrls: ['./gdpr-policy.component.scss'],
})
export class GdprPolicyComponent {
  public scrollTo(elementId: string): void {
    document.getElementById(elementId)?.scrollIntoView({
      behavior: 'smooth',
    });
  }
}
