import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GenreFormService } from './genre-form.service';
import { GenreService } from '../service/genre.service';
import { IGenre } from '../genre.model';

import { GenreUpdateComponent } from './genre-update.component';

describe('Genre Management Update Component', () => {
  let comp: GenreUpdateComponent;
  let fixture: ComponentFixture<GenreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let genreFormService: GenreFormService;
  let genreService: GenreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GenreUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(GenreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GenreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    genreFormService = TestBed.inject(GenreFormService);
    genreService = TestBed.inject(GenreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const genre: IGenre = { id: 456 };

      activatedRoute.data = of({ genre });
      comp.ngOnInit();

      expect(comp.genre).toEqual(genre);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenre>>();
      const genre = { id: 123 };
      jest.spyOn(genreFormService, 'getGenre').mockReturnValue(genre);
      jest.spyOn(genreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genre }));
      saveSubject.complete();

      // THEN
      expect(genreFormService.getGenre).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(genreService.update).toHaveBeenCalledWith(expect.objectContaining(genre));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenre>>();
      const genre = { id: 123 };
      jest.spyOn(genreFormService, 'getGenre').mockReturnValue({ id: null });
      jest.spyOn(genreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genre: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: genre }));
      saveSubject.complete();

      // THEN
      expect(genreFormService.getGenre).toHaveBeenCalled();
      expect(genreService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGenre>>();
      const genre = { id: 123 };
      jest.spyOn(genreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ genre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(genreService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
