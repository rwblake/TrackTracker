import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../sharing-preference.test-samples';

import { SharingPreferenceFormService } from './sharing-preference-form.service';

describe('SharingPreference Form Service', () => {
  let service: SharingPreferenceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SharingPreferenceFormService);
  });

  describe('Service methods', () => {
    describe('createSharingPreferenceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSharingPreferenceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            metric: expect.any(Object),
            visibility: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });

      it('passing ISharingPreference should create a new form with FormGroup', () => {
        const formGroup = service.createSharingPreferenceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            metric: expect.any(Object),
            visibility: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });
    });

    describe('getSharingPreference', () => {
      it('should return NewSharingPreference for default SharingPreference initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSharingPreferenceFormGroup(sampleWithNewData);

        const sharingPreference = service.getSharingPreference(formGroup) as any;

        expect(sharingPreference).toMatchObject(sampleWithNewData);
      });

      it('should return NewSharingPreference for empty SharingPreference initial value', () => {
        const formGroup = service.createSharingPreferenceFormGroup();

        const sharingPreference = service.getSharingPreference(formGroup) as any;

        expect(sharingPreference).toMatchObject({});
      });

      it('should return ISharingPreference', () => {
        const formGroup = service.createSharingPreferenceFormGroup(sampleWithRequiredData);

        const sharingPreference = service.getSharingPreference(formGroup) as any;

        expect(sharingPreference).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISharingPreference should not enable id FormControl', () => {
        const formGroup = service.createSharingPreferenceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSharingPreference should disable id FormControl', () => {
        const formGroup = service.createSharingPreferenceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
