import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { StreamService } from '../service/stream.service';

import { StreamComponent } from './stream.component';

describe('Stream Management Component', () => {
  let comp: StreamComponent;
  let fixture: ComponentFixture<StreamComponent>;
  let service: StreamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'stream', component: StreamComponent }]), HttpClientTestingModule],
      declarations: [StreamComponent],
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
      .overrideTemplate(StreamComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StreamComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(StreamService);

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
    expect(comp.streams?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to streamService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getStreamIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getStreamIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
