import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SpotifyTokenService } from '../service/spotify-token.service';

import { SpotifyTokenComponent } from './spotify-token.component';

describe('SpotifyToken Management Component', () => {
  let comp: SpotifyTokenComponent;
  let fixture: ComponentFixture<SpotifyTokenComponent>;
  let service: SpotifyTokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'spotify-token', component: SpotifyTokenComponent }]), HttpClientTestingModule],
      declarations: [SpotifyTokenComponent],
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
      .overrideTemplate(SpotifyTokenComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpotifyTokenComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SpotifyTokenService);

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
    expect(comp.spotifyTokens?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to spotifyTokenService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSpotifyTokenIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSpotifyTokenIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
