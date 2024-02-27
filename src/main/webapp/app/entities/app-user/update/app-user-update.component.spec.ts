import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AppUserFormService } from './app-user-form.service';
import { AppUserService } from '../service/app-user.service';
import { IAppUser } from '../app-user.model';
import { IUserPreferences } from 'app/entities/user-preferences/user-preferences.model';
import { UserPreferencesService } from 'app/entities/user-preferences/service/user-preferences.service';
import { ISpotifyToken } from 'app/entities/spotify-token/spotify-token.model';
import { SpotifyTokenService } from 'app/entities/spotify-token/service/spotify-token.service';
import { IFeed } from 'app/entities/feed/feed.model';
import { FeedService } from 'app/entities/feed/service/feed.service';

import { AppUserUpdateComponent } from './app-user-update.component';

describe('AppUser Management Update Component', () => {
  let comp: AppUserUpdateComponent;
  let fixture: ComponentFixture<AppUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let appUserFormService: AppUserFormService;
  let appUserService: AppUserService;
  let userPreferencesService: UserPreferencesService;
  let spotifyTokenService: SpotifyTokenService;
  let feedService: FeedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AppUserUpdateComponent],
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
      .overrideTemplate(AppUserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppUserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appUserFormService = TestBed.inject(AppUserFormService);
    appUserService = TestBed.inject(AppUserService);
    userPreferencesService = TestBed.inject(UserPreferencesService);
    spotifyTokenService = TestBed.inject(SpotifyTokenService);
    feedService = TestBed.inject(FeedService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call userPreferences query and add missing value', () => {
      const appUser: IAppUser = { id: 456 };
      const userPreferences: IUserPreferences = { id: 78557 };
      appUser.userPreferences = userPreferences;

      const userPreferencesCollection: IUserPreferences[] = [{ id: 3108 }];
      jest.spyOn(userPreferencesService, 'query').mockReturnValue(of(new HttpResponse({ body: userPreferencesCollection })));
      const expectedCollection: IUserPreferences[] = [userPreferences, ...userPreferencesCollection];
      jest.spyOn(userPreferencesService, 'addUserPreferencesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(userPreferencesService.query).toHaveBeenCalled();
      expect(userPreferencesService.addUserPreferencesToCollectionIfMissing).toHaveBeenCalledWith(
        userPreferencesCollection,
        userPreferences
      );
      expect(comp.userPreferencesCollection).toEqual(expectedCollection);
    });

    it('Should call spotifyToken query and add missing value', () => {
      const appUser: IAppUser = { id: 456 };
      const spotifyToken: ISpotifyToken = { id: 72969 };
      appUser.spotifyToken = spotifyToken;

      const spotifyTokenCollection: ISpotifyToken[] = [{ id: 42831 }];
      jest.spyOn(spotifyTokenService, 'query').mockReturnValue(of(new HttpResponse({ body: spotifyTokenCollection })));
      const expectedCollection: ISpotifyToken[] = [spotifyToken, ...spotifyTokenCollection];
      jest.spyOn(spotifyTokenService, 'addSpotifyTokenToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(spotifyTokenService.query).toHaveBeenCalled();
      expect(spotifyTokenService.addSpotifyTokenToCollectionIfMissing).toHaveBeenCalledWith(spotifyTokenCollection, spotifyToken);
      expect(comp.spotifyTokensCollection).toEqual(expectedCollection);
    });

    it('Should call feed query and add missing value', () => {
      const appUser: IAppUser = { id: 456 };
      const feed: IFeed = { id: 68587 };
      appUser.feed = feed;

      const feedCollection: IFeed[] = [{ id: 67722 }];
      jest.spyOn(feedService, 'query').mockReturnValue(of(new HttpResponse({ body: feedCollection })));
      const expectedCollection: IFeed[] = [feed, ...feedCollection];
      jest.spyOn(feedService, 'addFeedToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(feedService.query).toHaveBeenCalled();
      expect(feedService.addFeedToCollectionIfMissing).toHaveBeenCalledWith(feedCollection, feed);
      expect(comp.feedsCollection).toEqual(expectedCollection);
    });

    it('Should call AppUser query and add missing value', () => {
      const appUser: IAppUser = { id: 456 };
      const blockedByUser: IAppUser = { id: 68676 };
      appUser.blockedByUser = blockedByUser;

      const appUserCollection: IAppUser[] = [{ id: 50702 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [blockedByUser];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(expect.objectContaining)
      );
      expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const appUser: IAppUser = { id: 456 };
      const userPreferences: IUserPreferences = { id: 9236 };
      appUser.userPreferences = userPreferences;
      const spotifyToken: ISpotifyToken = { id: 43966 };
      appUser.spotifyToken = spotifyToken;
      const feed: IFeed = { id: 98528 };
      appUser.feed = feed;
      const blockedByUser: IAppUser = { id: 26769 };
      appUser.blockedByUser = blockedByUser;

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(comp.userPreferencesCollection).toContain(userPreferences);
      expect(comp.spotifyTokensCollection).toContain(spotifyToken);
      expect(comp.feedsCollection).toContain(feed);
      expect(comp.appUsersSharedCollection).toContain(blockedByUser);
      expect(comp.appUser).toEqual(appUser);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppUser>>();
      const appUser = { id: 123 };
      jest.spyOn(appUserFormService, 'getAppUser').mockReturnValue(appUser);
      jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appUser }));
      saveSubject.complete();

      // THEN
      expect(appUserFormService.getAppUser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(appUserService.update).toHaveBeenCalledWith(expect.objectContaining(appUser));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppUser>>();
      const appUser = { id: 123 };
      jest.spyOn(appUserFormService, 'getAppUser').mockReturnValue({ id: null });
      jest.spyOn(appUserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appUser }));
      saveSubject.complete();

      // THEN
      expect(appUserFormService.getAppUser).toHaveBeenCalled();
      expect(appUserService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAppUser>>();
      const appUser = { id: 123 };
      jest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appUserService.update).toHaveBeenCalled();
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

    describe('compareSpotifyToken', () => {
      it('Should forward to spotifyTokenService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(spotifyTokenService, 'compareSpotifyToken');
        comp.compareSpotifyToken(entity, entity2);
        expect(spotifyTokenService.compareSpotifyToken).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareFeed', () => {
      it('Should forward to feedService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(feedService, 'compareFeed');
        comp.compareFeed(entity, entity2);
        expect(feedService.compareFeed).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
