import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SongDetailComponent } from './song-detail.component';

describe('Song Management Detail Component', () => {
  let comp: SongDetailComponent;
  let fixture: ComponentFixture<SongDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SongDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ song: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SongDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SongDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load song on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.song).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
