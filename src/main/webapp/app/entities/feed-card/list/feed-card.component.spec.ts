import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FeedCardService } from '../service/feed-card.service';

import { FeedCardComponent } from './feed-card.component';

describe('FeedCard Management Component', () => {
  let comp: FeedCardComponent;
  let fixture: ComponentFixture<FeedCardComponent>;
  let service: FeedCardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'feed-card', component: FeedCardComponent }]), HttpClientTestingModule],
      declarations: [FeedCardComponent],
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
      .overrideTemplate(FeedCardComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FeedCardComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FeedCardService);

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
    expect(comp.feedCards?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to feedCardService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getFeedCardIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFeedCardIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
