import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FriendRecommendationFormService } from './friend-recommendation-form.service';
import { FriendRecommendationService } from '../service/friend-recommendation.service';
import { IFriendRecommendation } from '../friend-recommendation.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { FriendRecommendationUpdateComponent } from './friend-recommendation-update.component';

describe('FriendRecommendation Management Update Component', () => {
  let comp: FriendRecommendationUpdateComponent;
  let fixture: ComponentFixture<FriendRecommendationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let friendRecommendationFormService: FriendRecommendationFormService;
  let friendRecommendationService: FriendRecommendationService;
  let appUserService: AppUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FriendRecommendationUpdateComponent],
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
      .overrideTemplate(FriendRecommendationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FriendRecommendationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    friendRecommendationFormService = TestBed.inject(FriendRecommendationFormService);
    friendRecommendationService = TestBed.inject(FriendRecommendationService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call AppUser query and add missing value', () => {
      const friendRecommendation: IFriendRecommendation = { id: 456 };
      const forAppUser: IAppUser = { id: 19197 };
      friendRecommendation.forAppUser = forAppUser;

      const appUserCollection: IAppUser[] = [{ id: 68485 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [forAppUser];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendRecommendation });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(expect.objectContaining)
      );
      expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call aboutAppUser query and add missing value', () => {
      const friendRecommendation: IFriendRecommendation = { id: 456 };
      const aboutAppUser: IAppUser = { id: 13361 };
      friendRecommendation.aboutAppUser = aboutAppUser;

      const aboutAppUserCollection: IAppUser[] = [{ id: 38674 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: aboutAppUserCollection })));
      const expectedCollection: IAppUser[] = [aboutAppUser, ...aboutAppUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendRecommendation });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(aboutAppUserCollection, aboutAppUser);
      expect(comp.aboutAppUsersCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const friendRecommendation: IFriendRecommendation = { id: 456 };
      const aboutAppUser: IAppUser = { id: 7362 };
      friendRecommendation.aboutAppUser = aboutAppUser;
      const forAppUser: IAppUser = { id: 42575 };
      friendRecommendation.forAppUser = forAppUser;

      activatedRoute.data = of({ friendRecommendation });
      comp.ngOnInit();

      expect(comp.aboutAppUsersCollection).toContain(aboutAppUser);
      expect(comp.appUsersSharedCollection).toContain(forAppUser);
      expect(comp.friendRecommendation).toEqual(friendRecommendation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendRecommendation>>();
      const friendRecommendation = { id: 123 };
      jest.spyOn(friendRecommendationFormService, 'getFriendRecommendation').mockReturnValue(friendRecommendation);
      jest.spyOn(friendRecommendationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendRecommendation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendRecommendation }));
      saveSubject.complete();

      // THEN
      expect(friendRecommendationFormService.getFriendRecommendation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(friendRecommendationService.update).toHaveBeenCalledWith(expect.objectContaining(friendRecommendation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendRecommendation>>();
      const friendRecommendation = { id: 123 };
      jest.spyOn(friendRecommendationFormService, 'getFriendRecommendation').mockReturnValue({ id: null });
      jest.spyOn(friendRecommendationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendRecommendation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendRecommendation }));
      saveSubject.complete();

      // THEN
      expect(friendRecommendationFormService.getFriendRecommendation).toHaveBeenCalled();
      expect(friendRecommendationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendRecommendation>>();
      const friendRecommendation = { id: 123 };
      jest.spyOn(friendRecommendationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendRecommendation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(friendRecommendationService.update).toHaveBeenCalled();
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
