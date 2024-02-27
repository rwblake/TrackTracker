import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FriendRequestFormService } from './friend-request-form.service';
import { FriendRequestService } from '../service/friend-request.service';
import { IFriendRequest } from '../friend-request.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { FriendRequestUpdateComponent } from './friend-request-update.component';

describe('FriendRequest Management Update Component', () => {
  let comp: FriendRequestUpdateComponent;
  let fixture: ComponentFixture<FriendRequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let friendRequestFormService: FriendRequestFormService;
  let friendRequestService: FriendRequestService;
  let appUserService: AppUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FriendRequestUpdateComponent],
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
      .overrideTemplate(FriendRequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FriendRequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    friendRequestFormService = TestBed.inject(FriendRequestFormService);
    friendRequestService = TestBed.inject(FriendRequestService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call AppUser query and add missing value', () => {
      const friendRequest: IFriendRequest = { id: 456 };
      const toAppUser: IAppUser = { id: 81259 };
      friendRequest.toAppUser = toAppUser;

      const appUserCollection: IAppUser[] = [{ id: 95423 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [toAppUser];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendRequest });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(expect.objectContaining)
      );
      expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call initiatingAppUser query and add missing value', () => {
      const friendRequest: IFriendRequest = { id: 456 };
      const initiatingAppUser: IAppUser = { id: 78794 };
      friendRequest.initiatingAppUser = initiatingAppUser;

      const initiatingAppUserCollection: IAppUser[] = [{ id: 47694 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: initiatingAppUserCollection })));
      const expectedCollection: IAppUser[] = [initiatingAppUser, ...initiatingAppUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendRequest });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(initiatingAppUserCollection, initiatingAppUser);
      expect(comp.initiatingAppUsersCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const friendRequest: IFriendRequest = { id: 456 };
      const initiatingAppUser: IAppUser = { id: 8621 };
      friendRequest.initiatingAppUser = initiatingAppUser;
      const toAppUser: IAppUser = { id: 30103 };
      friendRequest.toAppUser = toAppUser;

      activatedRoute.data = of({ friendRequest });
      comp.ngOnInit();

      expect(comp.initiatingAppUsersCollection).toContain(initiatingAppUser);
      expect(comp.appUsersSharedCollection).toContain(toAppUser);
      expect(comp.friendRequest).toEqual(friendRequest);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendRequest>>();
      const friendRequest = { id: 123 };
      jest.spyOn(friendRequestFormService, 'getFriendRequest').mockReturnValue(friendRequest);
      jest.spyOn(friendRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendRequest }));
      saveSubject.complete();

      // THEN
      expect(friendRequestFormService.getFriendRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(friendRequestService.update).toHaveBeenCalledWith(expect.objectContaining(friendRequest));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendRequest>>();
      const friendRequest = { id: 123 };
      jest.spyOn(friendRequestFormService, 'getFriendRequest').mockReturnValue({ id: null });
      jest.spyOn(friendRequestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendRequest }));
      saveSubject.complete();

      // THEN
      expect(friendRequestFormService.getFriendRequest).toHaveBeenCalled();
      expect(friendRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendRequest>>();
      const friendRequest = { id: 123 };
      jest.spyOn(friendRequestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(friendRequestService.update).toHaveBeenCalled();
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
