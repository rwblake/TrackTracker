import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserPreferencesFormService } from './user-preferences-form.service';
import { UserPreferencesService } from '../service/user-preferences.service';
import { IUserPreferences } from '../user-preferences.model';

import { UserPreferencesUpdateComponent } from './user-preferences-update.component';

describe('UserPreferences Management Update Component', () => {
  let comp: UserPreferencesUpdateComponent;
  let fixture: ComponentFixture<UserPreferencesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userPreferencesFormService: UserPreferencesFormService;
  let userPreferencesService: UserPreferencesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserPreferencesUpdateComponent],
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
      .overrideTemplate(UserPreferencesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserPreferencesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userPreferencesFormService = TestBed.inject(UserPreferencesFormService);
    userPreferencesService = TestBed.inject(UserPreferencesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const userPreferences: IUserPreferences = { id: 456 };

      activatedRoute.data = of({ userPreferences });
      comp.ngOnInit();

      expect(comp.userPreferences).toEqual(userPreferences);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserPreferences>>();
      const userPreferences = { id: 123 };
      jest.spyOn(userPreferencesFormService, 'getUserPreferences').mockReturnValue(userPreferences);
      jest.spyOn(userPreferencesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPreferences });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userPreferences }));
      saveSubject.complete();

      // THEN
      expect(userPreferencesFormService.getUserPreferences).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userPreferencesService.update).toHaveBeenCalledWith(expect.objectContaining(userPreferences));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserPreferences>>();
      const userPreferences = { id: 123 };
      jest.spyOn(userPreferencesFormService, 'getUserPreferences').mockReturnValue({ id: null });
      jest.spyOn(userPreferencesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPreferences: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userPreferences }));
      saveSubject.complete();

      // THEN
      expect(userPreferencesFormService.getUserPreferences).toHaveBeenCalled();
      expect(userPreferencesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserPreferences>>();
      const userPreferences = { id: 123 };
      jest.spyOn(userPreferencesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userPreferences });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userPreferencesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
