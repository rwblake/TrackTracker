import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../stream.test-samples';

import { StreamFormService } from './stream-form.service';

describe('Stream Form Service', () => {
  let service: StreamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StreamFormService);
  });

  describe('Service methods', () => {
    describe('createStreamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStreamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            playedAt: expect.any(Object),
            song: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });

      it('passing IStream should create a new form with FormGroup', () => {
        const formGroup = service.createStreamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            playedAt: expect.any(Object),
            song: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });
    });

    describe('getStream', () => {
      it('should return NewStream for default Stream initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createStreamFormGroup(sampleWithNewData);

        const stream = service.getStream(formGroup) as any;

        expect(stream).toMatchObject(sampleWithNewData);
      });

      it('should return NewStream for empty Stream initial value', () => {
        const formGroup = service.createStreamFormGroup();

        const stream = service.getStream(formGroup) as any;

        expect(stream).toMatchObject({});
      });

      it('should return IStream', () => {
        const formGroup = service.createStreamFormGroup(sampleWithRequiredData);

        const stream = service.getStream(formGroup) as any;

        expect(stream).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStream should not enable id FormControl', () => {
        const formGroup = service.createStreamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStream should disable id FormControl', () => {
        const formGroup = service.createStreamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
