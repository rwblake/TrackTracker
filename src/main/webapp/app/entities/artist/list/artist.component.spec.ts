import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ArtistService } from '../service/artist.service';

import { ArtistComponent } from './artist.component';

describe('Artist Management Component', () => {
  let comp: ArtistComponent;
  let fixture: ComponentFixture<ArtistComponent>;
  let service: ArtistService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'artist', component: ArtistComponent }]), HttpClientTestingModule],
      declarations: [ArtistComponent],
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
      .overrideTemplate(ArtistComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ArtistComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ArtistService);

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
    expect(comp.artists?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to artistService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getArtistIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getArtistIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
