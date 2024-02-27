import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../playlist.test-samples';

import { PlaylistFormService } from './playlist-form.service';

describe('Playlist Form Service', () => {
  let service: PlaylistFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlaylistFormService);
  });

  describe('Service methods', () => {
    describe('createPlaylistFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlaylistFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            spotifyID: expect.any(Object),
            name: expect.any(Object),
            imageURL: expect.any(Object),
            playlistStats: expect.any(Object),
            songs: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });

      it('passing IPlaylist should create a new form with FormGroup', () => {
        const formGroup = service.createPlaylistFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            spotifyID: expect.any(Object),
            name: expect.any(Object),
            imageURL: expect.any(Object),
            playlistStats: expect.any(Object),
            songs: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });
    });

    describe('getPlaylist', () => {
      it('should return NewPlaylist for default Playlist initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPlaylistFormGroup(sampleWithNewData);

        const playlist = service.getPlaylist(formGroup) as any;

        expect(playlist).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlaylist for empty Playlist initial value', () => {
        const formGroup = service.createPlaylistFormGroup();

        const playlist = service.getPlaylist(formGroup) as any;

        expect(playlist).toMatchObject({});
      });

      it('should return IPlaylist', () => {
        const formGroup = service.createPlaylistFormGroup(sampleWithRequiredData);

        const playlist = service.getPlaylist(formGroup) as any;

        expect(playlist).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlaylist should not enable id FormControl', () => {
        const formGroup = service.createPlaylistFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlaylist should disable id FormControl', () => {
        const formGroup = service.createPlaylistFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
