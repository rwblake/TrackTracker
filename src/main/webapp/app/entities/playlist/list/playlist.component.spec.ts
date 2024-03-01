import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PlaylistService } from '../service/playlist.service';

import { PlaylistComponent } from './playlist.component';

describe('Playlist Management Component', () => {
  let comp: PlaylistComponent;
  let fixture: ComponentFixture<PlaylistComponent>;
  let service: PlaylistService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'playlist', component: PlaylistComponent }]), HttpClientTestingModule],
      declarations: [PlaylistComponent],
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
      .overrideTemplate(PlaylistComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlaylistComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PlaylistService);

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
    expect(comp.playlists?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to playlistService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getPlaylistIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getPlaylistIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
