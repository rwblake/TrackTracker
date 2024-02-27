import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CardTemplateDetailComponent } from './card-template-detail.component';

describe('CardTemplate Management Detail Component', () => {
  let comp: CardTemplateDetailComponent;
  let fixture: ComponentFixture<CardTemplateDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CardTemplateDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ cardTemplate: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CardTemplateDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CardTemplateDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cardTemplate on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.cardTemplate).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
