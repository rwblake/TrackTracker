import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SpotifyTokenFormService } from './spotify-token-form.service';
import { SpotifyTokenService } from '../service/spotify-token.service';
import { ISpotifyToken } from '../spotify-token.model';

import { SpotifyTokenUpdateComponent } from './spotify-token-update.component';

describe('SpotifyToken Management Update Component', () => {
  let comp: SpotifyTokenUpdateComponent;
  let fixture: ComponentFixture<SpotifyTokenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let spotifyTokenFormService: SpotifyTokenFormService;
  let spotifyTokenService: SpotifyTokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SpotifyTokenUpdateComponent],
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
      .overrideTemplate(SpotifyTokenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpotifyTokenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    spotifyTokenFormService = TestBed.inject(SpotifyTokenFormService);
    spotifyTokenService = TestBed.inject(SpotifyTokenService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const spotifyToken: ISpotifyToken = { id: 456 };

      activatedRoute.data = of({ spotifyToken });
      comp.ngOnInit();

      expect(comp.spotifyToken).toEqual(spotifyToken);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpotifyToken>>();
      const spotifyToken = { id: 123 };
      jest.spyOn(spotifyTokenFormService, 'getSpotifyToken').mockReturnValue(spotifyToken);
      jest.spyOn(spotifyTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spotifyToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spotifyToken }));
      saveSubject.complete();

      // THEN
      expect(spotifyTokenFormService.getSpotifyToken).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(spotifyTokenService.update).toHaveBeenCalledWith(expect.objectContaining(spotifyToken));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpotifyToken>>();
      const spotifyToken = { id: 123 };
      jest.spyOn(spotifyTokenFormService, 'getSpotifyToken').mockReturnValue({ id: null });
      jest.spyOn(spotifyTokenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spotifyToken: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spotifyToken }));
      saveSubject.complete();

      // THEN
      expect(spotifyTokenFormService.getSpotifyToken).toHaveBeenCalled();
      expect(spotifyTokenService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISpotifyToken>>();
      const spotifyToken = { id: 123 };
      jest.spyOn(spotifyTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spotifyToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(spotifyTokenService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
