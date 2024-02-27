import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../friend-recommendation.test-samples';

import { FriendRecommendationFormService } from './friend-recommendation-form.service';

describe('FriendRecommendation Form Service', () => {
  let service: FriendRecommendationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FriendRecommendationFormService);
  });

  describe('Service methods', () => {
    describe('createFriendRecommendationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFriendRecommendationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            similarity: expect.any(Object),
            createdAt: expect.any(Object),
            aboutAppUser: expect.any(Object),
            forAppUser: expect.any(Object),
          })
        );
      });

      it('passing IFriendRecommendation should create a new form with FormGroup', () => {
        const formGroup = service.createFriendRecommendationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            similarity: expect.any(Object),
            createdAt: expect.any(Object),
            aboutAppUser: expect.any(Object),
            forAppUser: expect.any(Object),
          })
        );
      });
    });

    describe('getFriendRecommendation', () => {
      it('should return NewFriendRecommendation for default FriendRecommendation initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFriendRecommendationFormGroup(sampleWithNewData);

        const friendRecommendation = service.getFriendRecommendation(formGroup) as any;

        expect(friendRecommendation).toMatchObject(sampleWithNewData);
      });

      it('should return NewFriendRecommendation for empty FriendRecommendation initial value', () => {
        const formGroup = service.createFriendRecommendationFormGroup();

        const friendRecommendation = service.getFriendRecommendation(formGroup) as any;

        expect(friendRecommendation).toMatchObject({});
      });

      it('should return IFriendRecommendation', () => {
        const formGroup = service.createFriendRecommendationFormGroup(sampleWithRequiredData);

        const friendRecommendation = service.getFriendRecommendation(formGroup) as any;

        expect(friendRecommendation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFriendRecommendation should not enable id FormControl', () => {
        const formGroup = service.createFriendRecommendationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFriendRecommendation should disable id FormControl', () => {
        const formGroup = service.createFriendRecommendationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
