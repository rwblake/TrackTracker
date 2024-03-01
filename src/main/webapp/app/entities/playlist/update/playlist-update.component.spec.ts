import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PlaylistFormService } from './playlist-form.service';
import { PlaylistService } from '../service/playlist.service';
import { IPlaylist } from '../playlist.model';
import { IPlaylistStats } from 'app/entities/playlist-stats/playlist-stats.model';
import { PlaylistStatsService } from 'app/entities/playlist-stats/service/playlist-stats.service';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';

import { PlaylistUpdateComponent } from './playlist-update.component';

describe('Playlist Management Update Component', () => {
  let comp: PlaylistUpdateComponent;
  let fixture: ComponentFixture<PlaylistUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playlistFormService: PlaylistFormService;
  let playlistService: PlaylistService;
  let playlistStatsService: PlaylistStatsService;
  let songService: SongService;
  let appUserService: AppUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PlaylistUpdateComponent],
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
      .overrideTemplate(PlaylistUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlaylistUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playlistFormService = TestBed.inject(PlaylistFormService);
    playlistService = TestBed.inject(PlaylistService);
    playlistStatsService = TestBed.inject(PlaylistStatsService);
    songService = TestBed.inject(SongService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call playlistStats query and add missing value', () => {
      const playlist: IPlaylist = { id: 456 };
      const playlistStats: IPlaylistStats = { id: 48396 };
      playlist.playlistStats = playlistStats;

      const playlistStatsCollection: IPlaylistStats[] = [{ id: 39242 }];
      jest.spyOn(playlistStatsService, 'query').mockReturnValue(of(new HttpResponse({ body: playlistStatsCollection })));
      const expectedCollection: IPlaylistStats[] = [playlistStats, ...playlistStatsCollection];
      jest.spyOn(playlistStatsService, 'addPlaylistStatsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      expect(playlistStatsService.query).toHaveBeenCalled();
      expect(playlistStatsService.addPlaylistStatsToCollectionIfMissing).toHaveBeenCalledWith(playlistStatsCollection, playlistStats);
      expect(comp.playlistStatsCollection).toEqual(expectedCollection);
    });

    it('Should call Song query and add missing value', () => {
      const playlist: IPlaylist = { id: 456 };
      const songs: ISong[] = [{ id: 5619 }];
      playlist.songs = songs;

      const songCollection: ISong[] = [{ id: 99100 }];
      jest.spyOn(songService, 'query').mockReturnValue(of(new HttpResponse({ body: songCollection })));
      const additionalSongs = [...songs];
      const expectedCollection: ISong[] = [...additionalSongs, ...songCollection];
      jest.spyOn(songService, 'addSongToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      expect(songService.query).toHaveBeenCalled();
      expect(songService.addSongToCollectionIfMissing).toHaveBeenCalledWith(
        songCollection,
        ...additionalSongs.map(expect.objectContaining)
      );
      expect(comp.songsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call AppUser query and add missing value', () => {
      const playlist: IPlaylist = { id: 456 };
      const appUser: IAppUser = { id: 17051 };
      playlist.appUser = appUser;

      const appUserCollection: IAppUser[] = [{ id: 89690 }];
      jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [appUser];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(expect.objectContaining)
      );
      expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const playlist: IPlaylist = { id: 456 };
      const playlistStats: IPlaylistStats = { id: 75780 };
      playlist.playlistStats = playlistStats;
      const song: ISong = { id: 49758 };
      playlist.songs = [song];
      const appUser: IAppUser = { id: 84568 };
      playlist.appUser = appUser;

      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      expect(comp.playlistStatsCollection).toContain(playlistStats);
      expect(comp.songsSharedCollection).toContain(song);
      expect(comp.appUsersSharedCollection).toContain(appUser);
      expect(comp.playlist).toEqual(playlist);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylist>>();
      const playlist = { id: 123 };
      jest.spyOn(playlistFormService, 'getPlaylist').mockReturnValue(playlist);
      jest.spyOn(playlistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playlist }));
      saveSubject.complete();

      // THEN
      expect(playlistFormService.getPlaylist).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playlistService.update).toHaveBeenCalledWith(expect.objectContaining(playlist));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylist>>();
      const playlist = { id: 123 };
      jest.spyOn(playlistFormService, 'getPlaylist').mockReturnValue({ id: null });
      jest.spyOn(playlistService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlist: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playlist }));
      saveSubject.complete();

      // THEN
      expect(playlistFormService.getPlaylist).toHaveBeenCalled();
      expect(playlistService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylist>>();
      const playlist = { id: 123 };
      jest.spyOn(playlistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playlistService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePlaylistStats', () => {
      it('Should forward to playlistStatsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playlistStatsService, 'comparePlaylistStats');
        comp.comparePlaylistStats(entity, entity2);
        expect(playlistStatsService.comparePlaylistStats).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
