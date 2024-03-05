import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SpotifyTokenDetailComponent } from './spotify-token-detail.component';

describe('SpotifyToken Management Detail Component', () => {
  let comp: SpotifyTokenDetailComponent;
  let fixture: ComponentFixture<SpotifyTokenDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SpotifyTokenDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ spotifyToken: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SpotifyTokenDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SpotifyTokenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load spotifyToken on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.spotifyToken).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
