import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ArtistFormService } from './artist-form.service';
import { ArtistService } from '../service/artist.service';
import { IArtist } from '../artist.model';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';
import { IAlbum } from 'app/entities/album/album.model';
import { AlbumService } from 'app/entities/album/service/album.service';
import { IGenre } from 'app/entities/genre/genre.model';
import { GenreService } from 'app/entities/genre/service/genre.service';

import { ArtistUpdateComponent } from './artist-update.component';

describe('Artist Management Update Component', () => {
  let comp: ArtistUpdateComponent;
  let fixture: ComponentFixture<ArtistUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let artistFormService: ArtistFormService;
  let artistService: ArtistService;
  let songService: SongService;
  let albumService: AlbumService;
  let genreService: GenreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ArtistUpdateComponent],
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
      .overrideTemplate(ArtistUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ArtistUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    artistFormService = TestBed.inject(ArtistFormService);
    artistService = TestBed.inject(ArtistService);
    songService = TestBed.inject(SongService);
    albumService = TestBed.inject(AlbumService);
    genreService = TestBed.inject(GenreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Song query and add missing value', () => {
      const artist: IArtist = { id: 456 };
      const songs: ISong[] = [{ id: 73093 }];
      artist.songs = songs;

      const songCollection: ISong[] = [{ id: 98699 }];
      jest.spyOn(songService, 'query').mockReturnValue(of(new HttpResponse({ body: songCollection })));
      const additionalSongs = [...songs];
      const expectedCollection: ISong[] = [...additionalSongs, ...songCollection];
      jest.spyOn(songService, 'addSongToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      expect(songService.query).toHaveBeenCalled();
      expect(songService.addSongToCollectionIfMissing).toHaveBeenCalledWith(
        songCollection,
        ...additionalSongs.map(expect.objectContaining)
      );
      expect(comp.songsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Album query and add missing value', () => {
      const artist: IArtist = { id: 456 };
      const albums: IAlbum[] = [{ id: 4085 }];
      artist.albums = albums;

      const albumCollection: IAlbum[] = [{ id: 98717 }];
      jest.spyOn(albumService, 'query').mockReturnValue(of(new HttpResponse({ body: albumCollection })));
      const additionalAlbums = [...albums];
      const expectedCollection: IAlbum[] = [...additionalAlbums, ...albumCollection];
      jest.spyOn(albumService, 'addAlbumToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      expect(albumService.query).toHaveBeenCalled();
      expect(albumService.addAlbumToCollectionIfMissing).toHaveBeenCalledWith(
        albumCollection,
        ...additionalAlbums.map(expect.objectContaining)
      );
      expect(comp.albumsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Genre query and add missing value', () => {
      const artist: IArtist = { id: 456 };
      const genres: IGenre[] = [{ id: 73877 }];
      artist.genres = genres;

      const genreCollection: IGenre[] = [{ id: 29517 }];
      jest.spyOn(genreService, 'query').mockReturnValue(of(new HttpResponse({ body: genreCollection })));
      const additionalGenres = [...genres];
      const expectedCollection: IGenre[] = [...additionalGenres, ...genreCollection];
      jest.spyOn(genreService, 'addGenreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      expect(genreService.query).toHaveBeenCalled();
      expect(genreService.addGenreToCollectionIfMissing).toHaveBeenCalledWith(
        genreCollection,
        ...additionalGenres.map(expect.objectContaining)
      );
      expect(comp.genresSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const artist: IArtist = { id: 456 };
      const song: ISong = { id: 93326 };
      artist.songs = [song];
      const album: IAlbum = { id: 32636 };
      artist.albums = [album];
      const genre: IGenre = { id: 71096 };
      artist.genres = [genre];

      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      expect(comp.songsSharedCollection).toContain(song);
      expect(comp.albumsSharedCollection).toContain(album);
      expect(comp.genresSharedCollection).toContain(genre);
      expect(comp.artist).toEqual(artist);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArtist>>();
      const artist = { id: 123 };
      jest.spyOn(artistFormService, 'getArtist').mockReturnValue(artist);
      jest.spyOn(artistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: artist }));
      saveSubject.complete();

      // THEN
      expect(artistFormService.getArtist).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(artistService.update).toHaveBeenCalledWith(expect.objectContaining(artist));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArtist>>();
      const artist = { id: 123 };
      jest.spyOn(artistFormService, 'getArtist').mockReturnValue({ id: null });
      jest.spyOn(artistService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ artist: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: artist }));
      saveSubject.complete();

      // THEN
      expect(artistFormService.getArtist).toHaveBeenCalled();
      expect(artistService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArtist>>();
      const artist = { id: 123 };
      jest.spyOn(artistService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ artist });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(artistService.update).toHaveBeenCalled();
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

    describe('compareAlbum', () => {
      it('Should forward to albumService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(albumService, 'compareAlbum');
        comp.compareAlbum(entity, entity2);
        expect(albumService.compareAlbum).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGenre', () => {
      it('Should forward to genreService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(genreService, 'compareGenre');
        comp.compareGenre(entity, entity2);
        expect(genreService.compareGenre).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
