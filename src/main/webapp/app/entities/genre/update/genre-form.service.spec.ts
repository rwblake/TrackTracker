import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../genre.test-samples';

import { GenreFormService } from './genre-form.service';

describe('Genre Form Service', () => {
  let service: GenreFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GenreFormService);
  });

  describe('Service methods', () => {
    describe('createGenreFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGenreFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            artists: expect.any(Object),
          })
        );
      });

      it('passing IGenre should create a new form with FormGroup', () => {
        const formGroup = service.createGenreFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            artists: expect.any(Object),
          })
        );
      });
    });

    describe('getGenre', () => {
      it('should return NewGenre for default Genre initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGenreFormGroup(sampleWithNewData);

        const genre = service.getGenre(formGroup) as any;

        expect(genre).toMatchObject(sampleWithNewData);
      });

      it('should return NewGenre for empty Genre initial value', () => {
        const formGroup = service.createGenreFormGroup();

        const genre = service.getGenre(formGroup) as any;

        expect(genre).toMatchObject({});
      });

      it('should return IGenre', () => {
        const formGroup = service.createGenreFormGroup(sampleWithRequiredData);

        const genre = service.getGenre(formGroup) as any;

        expect(genre).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGenre should not enable id FormControl', () => {
        const formGroup = service.createGenreFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGenre should disable id FormControl', () => {
        const formGroup = service.createGenreFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
