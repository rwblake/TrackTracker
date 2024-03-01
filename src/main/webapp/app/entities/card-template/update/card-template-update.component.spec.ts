import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CardTemplateFormService } from './card-template-form.service';
import { CardTemplateService } from '../service/card-template.service';
import { ICardTemplate } from '../card-template.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { CardTemplateUpdateComponent } from './card-template-update.component';

describe('CardTemplate Management Update Component', () => {
  let comp: CardTemplateUpdateComponent;
  let fixture: ComponentFixture<CardTemplateUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cardTemplateFormService: CardTemplateFormService;
  let cardTemplateService: CardTemplateService;
  let appUserService: AppUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CardTemplateUpdateComponent],
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
      .overrideTemplate(CardTemplateUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CardTemplateUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cardTemplateFormService = TestBed.inject(CardTemplateFormService);
    cardTemplateService = TestBed.inject(CardTemplateService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call AppUser query and add missing value', () => {
      const cardTemplate: ICardTemplate = { id: 456 };
      const appUser: IAppUser = { id: 90663 };
      cardTemplate.appUser = appUser;

      const appUserCollection: IAppUser[] = [{ id: 83214 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [appUser];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cardTemplate });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(expect.objectContaining)
      );
      expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const cardTemplate: ICardTemplate = { id: 456 };
      const appUser: IAppUser = { id: 6437 };
      cardTemplate.appUser = appUser;

      activatedRoute.data = of({ cardTemplate });
      comp.ngOnInit();

      expect(comp.appUsersSharedCollection).toContain(appUser);
      expect(comp.cardTemplate).toEqual(cardTemplate);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardTemplate>>();
      const cardTemplate = { id: 123 };
      jest.spyOn(cardTemplateFormService, 'getCardTemplate').mockReturnValue(cardTemplate);
      jest.spyOn(cardTemplateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardTemplate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cardTemplate }));
      saveSubject.complete();

      // THEN
      expect(cardTemplateFormService.getCardTemplate).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cardTemplateService.update).toHaveBeenCalledWith(expect.objectContaining(cardTemplate));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardTemplate>>();
      const cardTemplate = { id: 123 };
      jest.spyOn(cardTemplateFormService, 'getCardTemplate').mockReturnValue({ id: null });
      jest.spyOn(cardTemplateService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardTemplate: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cardTemplate }));
      saveSubject.complete();

      // THEN
      expect(cardTemplateFormService.getCardTemplate).toHaveBeenCalled();
      expect(cardTemplateService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardTemplate>>();
      const cardTemplate = { id: 123 };
      jest.spyOn(cardTemplateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardTemplate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cardTemplateService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAppUser', () => {
      it('Should forward to appUserService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(appUserService, 'compareAppUser');
        comp.compareAppUser(entity, entity2);
        expect(appUserService.compareAppUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
