import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-preferences.test-samples';

import { UserPreferencesFormService } from './user-preferences-form.service';

describe('UserPreferences Form Service', () => {
  let service: UserPreferencesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserPreferencesFormService);
  });

  describe('Service methods', () => {
    describe('createUserPreferencesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserPreferencesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            visibility: expect.any(Object),
            isDarkMode: expect.any(Object),
            isHighContrast: expect.any(Object),
            playlistPrivacy: expect.any(Object),
          })
        );
      });

      it('passing IUserPreferences should create a new form with FormGroup', () => {
        const formGroup = service.createUserPreferencesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            visibility: expect.any(Object),
            isDarkMode: expect.any(Object),
            isHighContrast: expect.any(Object),
            playlistPrivacy: expect.any(Object),
          })
        );
      });
    });

    describe('getUserPreferences', () => {
      it('should return NewUserPreferences for default UserPreferences initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserPreferencesFormGroup(sampleWithNewData);

        const userPreferences = service.getUserPreferences(formGroup) as any;

        expect(userPreferences).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserPreferences for empty UserPreferences initial value', () => {
        const formGroup = service.createUserPreferencesFormGroup();

        const userPreferences = service.getUserPreferences(formGroup) as any;

        expect(userPreferences).toMatchObject({});
      });

      it('should return IUserPreferences', () => {
        const formGroup = service.createUserPreferencesFormGroup(sampleWithRequiredData);

        const userPreferences = service.getUserPreferences(formGroup) as any;

        expect(userPreferences).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserPreferences should not enable id FormControl', () => {
        const formGroup = service.createUserPreferencesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserPreferences should disable id FormControl', () => {
        const formGroup = service.createUserPreferencesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
