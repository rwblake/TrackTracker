import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../playlist-stats.test-samples';

import { PlaylistStatsFormService } from './playlist-stats-form.service';

describe('PlaylistStats Form Service', () => {
  let service: PlaylistStatsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlaylistStatsFormService);
  });

  describe('Service methods', () => {
    describe('createPlaylistStatsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlaylistStatsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            playlistLength: expect.any(Object),
            lastUpdated: expect.any(Object),
            happiestSong: expect.any(Object),
            fastestSong: expect.any(Object),
            sumsUpSong: expect.any(Object),
            anonmalousSong: expect.any(Object),
          })
        );
      });

      it('passing IPlaylistStats should create a new form with FormGroup', () => {
        const formGroup = service.createPlaylistStatsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            playlistLength: expect.any(Object),
            lastUpdated: expect.any(Object),
            happiestSong: expect.any(Object),
            fastestSong: expect.any(Object),
            sumsUpSong: expect.any(Object),
            anonmalousSong: expect.any(Object),
          })
        );
      });
    });

    describe('getPlaylistStats', () => {
      it('should return NewPlaylistStats for default PlaylistStats initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPlaylistStatsFormGroup(sampleWithNewData);

        const playlistStats = service.getPlaylistStats(formGroup) as any;

        expect(playlistStats).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlaylistStats for empty PlaylistStats initial value', () => {
        const formGroup = service.createPlaylistStatsFormGroup();

        const playlistStats = service.getPlaylistStats(formGroup) as any;

        expect(playlistStats).toMatchObject({});
      });

      it('should return IPlaylistStats', () => {
        const formGroup = service.createPlaylistStatsFormGroup(sampleWithRequiredData);

        const playlistStats = service.getPlaylistStats(formGroup) as any;

        expect(playlistStats).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlaylistStats should not enable id FormControl', () => {
        const formGroup = service.createPlaylistStatsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlaylistStats should disable id FormControl', () => {
        const formGroup = service.createPlaylistStatsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
