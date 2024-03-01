import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CardMetricService } from '../service/card-metric.service';

import { CardMetricComponent } from './card-metric.component';

describe('CardMetric Management Component', () => {
  let comp: CardMetricComponent;
  let fixture: ComponentFixture<CardMetricComponent>;
  let service: CardMetricService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'card-metric', component: CardMetricComponent }]), HttpClientTestingModule],
      declarations: [CardMetricComponent],
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
      .overrideTemplate(CardMetricComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CardMetricComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CardMetricService);

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
    expect(comp.cardMetrics?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to cardMetricService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCardMetricIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCardMetricIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
