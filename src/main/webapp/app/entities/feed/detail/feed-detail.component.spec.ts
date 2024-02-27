import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FeedDetailComponent } from './feed-detail.component';

describe('Feed Management Detail Component', () => {
  let comp: FeedDetailComponent;
  let fixture: ComponentFixture<FeedDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FeedDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ feed: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FeedDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FeedDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load feed on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.feed).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
