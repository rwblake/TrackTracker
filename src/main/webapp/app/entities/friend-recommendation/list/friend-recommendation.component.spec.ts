import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FriendRecommendationService } from '../service/friend-recommendation.service';

import { FriendRecommendationComponent } from './friend-recommendation.component';

describe('FriendRecommendation Management Component', () => {
  let comp: FriendRecommendationComponent;
  let fixture: ComponentFixture<FriendRecommendationComponent>;
  let service: FriendRecommendationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'friend-recommendation', component: FriendRecommendationComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [FriendRecommendationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(FriendRecommendationComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FriendRecommendationComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FriendRecommendationService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.friendRecommendations?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to friendRecommendationService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getFriendRecommendationIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFriendRecommendationIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
