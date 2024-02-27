import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PlaylistStatsDetailComponent } from './playlist-stats-detail.component';

describe('PlaylistStats Management Detail Component', () => {
  let comp: PlaylistStatsDetailComponent;
  let fixture: ComponentFixture<PlaylistStatsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlaylistStatsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ playlistStats: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PlaylistStatsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlaylistStatsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load playlistStats on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.playlistStats).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
