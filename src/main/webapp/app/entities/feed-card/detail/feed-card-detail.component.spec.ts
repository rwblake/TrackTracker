import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FeedCardDetailComponent } from './feed-card-detail.component';

describe('FeedCard Management Detail Component', () => {
  let comp: FeedCardDetailComponent;
  let fixture: ComponentFixture<FeedCardDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FeedCardDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ feedCard: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FeedCardDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FeedCardDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load feedCard on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.feedCard).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
