import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SharingPreferenceFormService } from './sharing-preference-form.service';
import { SharingPreferenceService } from '../service/sharing-preference.service';
import { ISharingPreference } from '../sharing-preference.model';
import { IUserPreferences } from 'app/entities/user-preferences/user-preferences.model';
import { UserPreferencesService } from 'app/entities/user-preferences/service/user-preferences.service';

import { SharingPreferenceUpdateComponent } from './sharing-preference-update.component';

describe('SharingPreference Management Update Component', () => {
  let comp: SharingPreferenceUpdateComponent;
  let fixture: ComponentFixture<SharingPreferenceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sharingPreferenceFormService: SharingPreferenceFormService;
  let sharingPreferenceService: SharingPreferenceService;
  let userPreferencesService: UserPreferencesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SharingPreferenceUpdateComponent],
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
      .overrideTemplate(SharingPreferenceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SharingPreferenceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sharingPreferenceFormService = TestBed.inject(SharingPreferenceFormService);
    sharingPreferenceService = TestBed.inject(SharingPreferenceService);
    userPreferencesService = TestBed.inject(UserPreferencesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserPreferences query and add missing value', () => {
      const sharingPreference: ISharingPreference = { id: 456 };
      const appUser: IUserPreferences = { id: 78241 };
      sharingPreference.appUser = appUser;

      const userPreferencesCollection: IUserPreferences[] = [{ id: 23419 }];
      jest.spyOn(userPreferencesService, 'query').mockReturnValue(of(new HttpResponse({ body: userPreferencesCollection })));
      const additionalUserPreferences = [appUser];
      const expectedCollection: IUserPreferences[] = [...additionalUserPreferences, ...userPreferencesCollection];
      jest.spyOn(userPreferencesService, 'addUserPreferencesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sharingPreference });
      comp.ngOnInit();

      expect(userPreferencesService.query).toHaveBeenCalled();
      expect(userPreferencesService.addUserPreferencesToCollectionIfMissing).toHaveBeenCalledWith(
        userPreferencesCollection,
        ...additionalUserPreferences.map(expect.objectContaining)
      );
      expect(comp.userPreferencesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sharingPreference: ISharingPreference = { id: 456 };
      const appUser: IUserPreferences = { id: 5640 };
      sharingPreference.appUser = appUser;

      activatedRoute.data = of({ sharingPreference });
      comp.ngOnInit();

      expect(comp.userPreferencesSharedCollection).toContain(appUser);
      expect(comp.sharingPreference).toEqual(sharingPreference);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISharingPreference>>();
      const sharingPreference = { id: 123 };
      jest.spyOn(sharingPreferenceFormService, 'getSharingPreference').mockReturnValue(sharingPreference);
      jest.spyOn(sharingPreferenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sharingPreference });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sharingPreference }));
      saveSubject.complete();

      // THEN
      expect(sharingPreferenceFormService.getSharingPreference).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sharingPreferenceService.update).toHaveBeenCalledWith(expect.objectContaining(sharingPreference));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISharingPreference>>();
      const sharingPreference = { id: 123 };
      jest.spyOn(sharingPreferenceFormService, 'getSharingPreference').mockReturnValue({ id: null });
      jest.spyOn(sharingPreferenceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sharingPreference: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sharingPreference }));
      saveSubject.complete();

      // THEN
      expect(sharingPreferenceFormService.getSharingPreference).toHaveBeenCalled();
      expect(sharingPreferenceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISharingPreference>>();
      const sharingPreference = { id: 123 };
      jest.spyOn(sharingPreferenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sharingPreference });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sharingPreferenceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserPreferences', () => {
      it('Should forward to userPreferencesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userPreferencesService, 'compareUserPreferences');
        comp.compareUserPreferences(entity, entity2);
        expect(userPreferencesService.compareUserPreferences).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
