import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../artist.test-samples';

import { ArtistFormService } from './artist-form.service';

describe('Artist Form Service', () => {
  let service: ArtistFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ArtistFormService);
  });

  describe('Service methods', () => {
    describe('createArtistFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createArtistFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            spotifyID: expect.any(Object),
            name: expect.any(Object),
            imageURL: expect.any(Object),
            songs: expect.any(Object),
            albums: expect.any(Object),
            genres: expect.any(Object),
          })
        );
      });

      it('passing IArtist should create a new form with FormGroup', () => {
        const formGroup = service.createArtistFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            spotifyID: expect.any(Object),
            name: expect.any(Object),
            imageURL: expect.any(Object),
            songs: expect.any(Object),
            albums: expect.any(Object),
            genres: expect.any(Object),
          })
        );
      });
    });

    describe('getArtist', () => {
      it('should return NewArtist for default Artist initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createArtistFormGroup(sampleWithNewData);

        const artist = service.getArtist(formGroup) as any;

        expect(artist).toMatchObject(sampleWithNewData);
      });

      it('should return NewArtist for empty Artist initial value', () => {
        const formGroup = service.createArtistFormGroup();

        const artist = service.getArtist(formGroup) as any;

        expect(artist).toMatchObject({});
      });

      it('should return IArtist', () => {
        const formGroup = service.createArtistFormGroup(sampleWithRequiredData);

        const artist = service.getArtist(formGroup) as any;

        expect(artist).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IArtist should not enable id FormControl', () => {
        const formGroup = service.createArtistFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewArtist should disable id FormControl', () => {
        const formGroup = service.createArtistFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
