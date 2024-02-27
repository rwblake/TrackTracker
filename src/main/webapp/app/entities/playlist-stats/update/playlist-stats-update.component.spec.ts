import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PlaylistStatsFormService } from './playlist-stats-form.service';
import { PlaylistStatsService } from '../service/playlist-stats.service';
import { IPlaylistStats } from '../playlist-stats.model';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';

import { PlaylistStatsUpdateComponent } from './playlist-stats-update.component';

describe('PlaylistStats Management Update Component', () => {
  let comp: PlaylistStatsUpdateComponent;
  let fixture: ComponentFixture<PlaylistStatsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playlistStatsFormService: PlaylistStatsFormService;
  let playlistStatsService: PlaylistStatsService;
  let songService: SongService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PlaylistStatsUpdateComponent],
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
      .overrideTemplate(PlaylistStatsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlaylistStatsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playlistStatsFormService = TestBed.inject(PlaylistStatsFormService);
    playlistStatsService = TestBed.inject(PlaylistStatsService);
    songService = TestBed.inject(SongService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Song query and add missing value', () => {
      const playlistStats: IPlaylistStats = { id: 456 };
      const happiestSong: ISong = { id: 52160 };
      playlistStats.happiestSong = happiestSong;
      const fastestSong: ISong = { id: 34337 };
      playlistStats.fastestSong = fastestSong;
      const sumsUpSong: ISong = { id: 21924 };
      playlistStats.sumsUpSong = sumsUpSong;
      const anonmalousSong: ISong = { id: 37883 };
      playlistStats.anonmalousSong = anonmalousSong;

      const songCollection: ISong[] = [{ id: 49065 }];
      jest.spyOn(songService, 'query').mockReturnValue(of(new HttpResponse({ body: songCollection })));
      const additionalSongs = [happiestSong, fastestSong, sumsUpSong, anonmalousSong];
      const expectedCollection: ISong[] = [...additionalSongs, ...songCollection];
      jest.spyOn(songService, 'addSongToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playlistStats });
      comp.ngOnInit();

      expect(songService.query).toHaveBeenCalled();
      expect(songService.addSongToCollectionIfMissing).toHaveBeenCalledWith(
        songCollection,
        ...additionalSongs.map(expect.objectContaining)
      );
      expect(comp.songsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const playlistStats: IPlaylistStats = { id: 456 };
      const happiestSong: ISong = { id: 22769 };
      playlistStats.happiestSong = happiestSong;
      const fastestSong: ISong = { id: 65168 };
      playlistStats.fastestSong = fastestSong;
      const sumsUpSong: ISong = { id: 47594 };
      playlistStats.sumsUpSong = sumsUpSong;
      const anonmalousSong: ISong = { id: 20607 };
      playlistStats.anonmalousSong = anonmalousSong;

      activatedRoute.data = of({ playlistStats });
      comp.ngOnInit();

      expect(comp.songsSharedCollection).toContain(happiestSong);
      expect(comp.songsSharedCollection).toContain(fastestSong);
      expect(comp.songsSharedCollection).toContain(sumsUpSong);
      expect(comp.songsSharedCollection).toContain(anonmalousSong);
      expect(comp.playlistStats).toEqual(playlistStats);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylistStats>>();
      const playlistStats = { id: 123 };
      jest.spyOn(playlistStatsFormService, 'getPlaylistStats').mockReturnValue(playlistStats);
      jest.spyOn(playlistStatsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlistStats });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playlistStats }));
      saveSubject.complete();

      // THEN
      expect(playlistStatsFormService.getPlaylistStats).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playlistStatsService.update).toHaveBeenCalledWith(expect.objectContaining(playlistStats));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylistStats>>();
      const playlistStats = { id: 123 };
      jest.spyOn(playlistStatsFormService, 'getPlaylistStats').mockReturnValue({ id: null });
      jest.spyOn(playlistStatsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlistStats: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playlistStats }));
      saveSubject.complete();

      // THEN
      expect(playlistStatsFormService.getPlaylistStats).toHaveBeenCalled();
      expect(playlistStatsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlaylistStats>>();
      const playlistStats = { id: 123 };
      jest.spyOn(playlistStatsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playlistStats });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playlistStatsService.update).toHaveBeenCalled();
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
  });
});
