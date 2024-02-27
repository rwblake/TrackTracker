import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PlaylistDetailComponent } from './playlist-detail.component';

describe('Playlist Management Detail Component', () => {
  let comp: PlaylistDetailComponent;
  let fixture: ComponentFixture<PlaylistDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlaylistDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ playlist: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PlaylistDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlaylistDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load playlist on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.playlist).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
