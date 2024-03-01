import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../spotify-token.test-samples';

import { SpotifyTokenFormService } from './spotify-token-form.service';

describe('SpotifyToken Form Service', () => {
  let service: SpotifyTokenFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SpotifyTokenFormService);
  });

  describe('Service methods', () => {
    describe('createSpotifyTokenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSpotifyTokenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            accessToken: expect.any(Object),
            tokenType: expect.any(Object),
            userScope: expect.any(Object),
            expires: expect.any(Object),
            refreshToken: expect.any(Object),
          })
        );
      });

      it('passing ISpotifyToken should create a new form with FormGroup', () => {
        const formGroup = service.createSpotifyTokenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            accessToken: expect.any(Object),
            tokenType: expect.any(Object),
            userScope: expect.any(Object),
            expires: expect.any(Object),
            refreshToken: expect.any(Object),
          })
        );
      });
    });

    describe('getSpotifyToken', () => {
      it('should return NewSpotifyToken for default SpotifyToken initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSpotifyTokenFormGroup(sampleWithNewData);

        const spotifyToken = service.getSpotifyToken(formGroup) as any;

        expect(spotifyToken).toMatchObject(sampleWithNewData);
      });

      it('should return NewSpotifyToken for empty SpotifyToken initial value', () => {
        const formGroup = service.createSpotifyTokenFormGroup();

        const spotifyToken = service.getSpotifyToken(formGroup) as any;

        expect(spotifyToken).toMatchObject({});
      });

      it('should return ISpotifyToken', () => {
        const formGroup = service.createSpotifyTokenFormGroup(sampleWithRequiredData);

        const spotifyToken = service.getSpotifyToken(formGroup) as any;

        expect(spotifyToken).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISpotifyToken should not enable id FormControl', () => {
        const formGroup = service.createSpotifyTokenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSpotifyToken should disable id FormControl', () => {
        const formGroup = service.createSpotifyTokenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
