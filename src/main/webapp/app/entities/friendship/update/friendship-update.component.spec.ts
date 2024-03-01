import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FriendshipFormService } from './friendship-form.service';
import { FriendshipService } from '../service/friendship.service';
import { IFriendship } from '../friendship.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { FriendshipUpdateComponent } from './friendship-update.component';

describe('Friendship Management Update Component', () => {
  let comp: FriendshipUpdateComponent;
  let fixture: ComponentFixture<FriendshipUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let friendshipFormService: FriendshipFormService;
  let friendshipService: FriendshipService;
  let appUserService: AppUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FriendshipUpdateComponent],
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
      .overrideTemplate(FriendshipUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FriendshipUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    friendshipFormService = TestBed.inject(FriendshipFormService);
    friendshipService = TestBed.inject(FriendshipService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call AppUser query and add missing value', () => {
      const friendship: IFriendship = { id: 456 };
      const appUser: IAppUser = { id: 87140 };
      friendship.appUser = appUser;

      const appUserCollection: IAppUser[] = [{ id: 69504 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [appUser];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(expect.objectContaining)
      );
      expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call friendInitiating query and add missing value', () => {
      const friendship: IFriendship = { id: 456 };
      const friendInitiating: IAppUser = { id: 79501 };
      friendship.friendInitiating = friendInitiating;

      const friendInitiatingCollection: IAppUser[] = [{ id: 66539 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: friendInitiatingCollection })));
      const expectedCollection: IAppUser[] = [friendInitiating, ...friendInitiatingCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(friendInitiatingCollection, friendInitiating);
      expect(comp.friendInitiatingsCollection).toEqual(expectedCollection);
    });

    it('Should call friendAccepting query and add missing value', () => {
      const friendship: IFriendship = { id: 456 };
      const friendAccepting: IAppUser = { id: 21681 };
      friendship.friendAccepting = friendAccepting;

      const friendAcceptingCollection: IAppUser[] = [{ id: 8040 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: friendAcceptingCollection })));
      const expectedCollection: IAppUser[] = [friendAccepting, ...friendAcceptingCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(friendAcceptingCollection, friendAccepting);
      expect(comp.friendAcceptingsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const friendship: IFriendship = { id: 456 };
      const friendInitiating: IAppUser = { id: 39840 };
      friendship.friendInitiating = friendInitiating;
      const friendAccepting: IAppUser = { id: 76003 };
      friendship.friendAccepting = friendAccepting;
      const appUser: IAppUser = { id: 23050 };
      friendship.appUser = appUser;

      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      expect(comp.friendInitiatingsCollection).toContain(friendInitiating);
      expect(comp.friendAcceptingsCollection).toContain(friendAccepting);
      expect(comp.appUsersSharedCollection).toContain(appUser);
      expect(comp.friendship).toEqual(friendship);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendship>>();
      const friendship = { id: 123 };
      jest.spyOn(friendshipFormService, 'getFriendship').mockReturnValue(friendship);
      jest.spyOn(friendshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendship }));
      saveSubject.complete();

      // THEN
      expect(friendshipFormService.getFriendship).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(friendshipService.update).toHaveBeenCalledWith(expect.objectContaining(friendship));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendship>>();
      const friendship = { id: 123 };
      jest.spyOn(friendshipFormService, 'getFriendship').mockReturnValue({ id: null });
      jest.spyOn(friendshipService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendship: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: friendship }));
      saveSubject.complete();

      // THEN
      expect(friendshipFormService.getFriendship).toHaveBeenCalled();
      expect(friendshipService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFriendship>>();
      const friendship = { id: 123 };
      jest.spyOn(friendshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ friendship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(friendshipService.update).toHaveBeenCalled();
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
