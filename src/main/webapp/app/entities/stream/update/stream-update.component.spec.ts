import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StreamFormService } from './stream-form.service';
import { StreamService } from '../service/stream.service';
import { IStream } from '../stream.model';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { StreamUpdateComponent } from './stream-update.component';

describe('Stream Management Update Component', () => {
  let comp: StreamUpdateComponent;
  let fixture: ComponentFixture<StreamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let streamFormService: StreamFormService;
  let streamService: StreamService;
  let songService: SongService;
  let appUserService: AppUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StreamUpdateComponent],
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
      .overrideTemplate(StreamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StreamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    streamFormService = TestBed.inject(StreamFormService);
    streamService = TestBed.inject(StreamService);
    songService = TestBed.inject(SongService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Song query and add missing value', () => {
      const stream: IStream = { id: 456 };
      const song: ISong = { id: 5084 };
      stream.song = song;

      const songCollection: ISong[] = [{ id: 48538 }];
      jest.spyOn(songService, 'query').mockReturnValue(of(new HttpResponse({ body: songCollection })));
      const additionalSongs = [song];
      const expectedCollection: ISong[] = [...additionalSongs, ...songCollection];
      jest.spyOn(songService, 'addSongToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stream });
      comp.ngOnInit();

      expect(songService.query).toHaveBeenCalled();
      expect(songService.addSongToCollectionIfMissing).toHaveBeenCalledWith(
        songCollection,
        ...additionalSongs.map(expect.objectContaining)
      );
      expect(comp.songsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call AppUser query and add missing value', () => {
      const stream: IStream = { id: 456 };
      const appUser: IAppUser = { id: 74197 };
      stream.appUser = appUser;

      const appUserCollection: IAppUser[] = [{ id: 29441 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [appUser];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stream });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(expect.objectContaining)
      );
      expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const stream: IStream = { id: 456 };
      const song: ISong = { id: 82154 };
      stream.song = song;
      const appUser: IAppUser = { id: 97184 };
      stream.appUser = appUser;

      activatedRoute.data = of({ stream });
      comp.ngOnInit();

      expect(comp.songsSharedCollection).toContain(song);
      expect(comp.appUsersSharedCollection).toContain(appUser);
      expect(comp.stream).toEqual(stream);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStream>>();
      const stream = { id: 123 };
      jest.spyOn(streamFormService, 'getStream').mockReturnValue(stream);
      jest.spyOn(streamService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stream });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stream }));
      saveSubject.complete();

      // THEN
      expect(streamFormService.getStream).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(streamService.update).toHaveBeenCalledWith(expect.objectContaining(stream));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStream>>();
      const stream = { id: 123 };
      jest.spyOn(streamFormService, 'getStream').mockReturnValue({ id: null });
      jest.spyOn(streamService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stream: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stream }));
      saveSubject.complete();

      // THEN
      expect(streamFormService.getStream).toHaveBeenCalled();
      expect(streamService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStream>>();
      const stream = { id: 123 };
      jest.spyOn(streamService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stream });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(streamService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSong', () => {
      it('Should forward to songService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(songService, 'compareSong');
        comp.compareSong(entity, entity2);
        expect(songService.compareSong).toHaveBeenCalledWith(entity, entity2);
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
