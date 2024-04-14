import { Component, inject, TemplateRef } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
})
export class FooterComponent {
  private modalService = inject(NgbModal);

  openModal(content: TemplateRef<any>) {
    this.modalService.open(content, { scrollable: true });
  }
}
