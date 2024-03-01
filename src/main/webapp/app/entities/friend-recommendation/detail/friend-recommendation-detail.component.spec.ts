import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FriendRecommendationDetailComponent } from './friend-recommendation-detail.component';

describe('FriendRecommendation Management Detail Component', () => {
  let comp: FriendRecommendationDetailComponent;
  let fixture: ComponentFixture<FriendRecommendationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FriendRecommendationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ friendRecommendation: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FriendRecommendationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FriendRecommendationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load friendRecommendation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.friendRecommendation).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
