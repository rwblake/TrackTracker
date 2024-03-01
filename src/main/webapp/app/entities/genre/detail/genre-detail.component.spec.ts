import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GenreDetailComponent } from './genre-detail.component';

describe('Genre Management Detail Component', () => {
  let comp: GenreDetailComponent;
  let fixture: ComponentFixture<GenreDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenreDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ genre: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GenreDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GenreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load genre on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.genre).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
