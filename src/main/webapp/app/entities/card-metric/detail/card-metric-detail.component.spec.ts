import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CardMetricDetailComponent } from './card-metric-detail.component';

describe('CardMetric Management Detail Component', () => {
  let comp: CardMetricDetailComponent;
  let fixture: ComponentFixture<CardMetricDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CardMetricDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ cardMetric: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CardMetricDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CardMetricDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cardMetric on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.cardMetric).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
