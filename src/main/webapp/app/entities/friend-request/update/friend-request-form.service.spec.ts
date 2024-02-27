import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../friend-request.test-samples';

import { FriendRequestFormService } from './friend-request-form.service';

describe('FriendRequest Form Service', () => {
  let service: FriendRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FriendRequestFormService);
  });

  describe('Service methods', () => {
    describe('createFriendRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFriendRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            createdAt: expect.any(Object),
            initiatingAppUser: expect.any(Object),
            toAppUser: expect.any(Object),
          })
        );
      });

      it('passing IFriendRequest should create a new form with FormGroup', () => {
        const formGroup = service.createFriendRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            createdAt: expect.any(Object),
            initiatingAppUser: expect.any(Object),
            toAppUser: expect.any(Object),
          })
        );
      });
    });

    describe('getFriendRequest', () => {
      it('should return NewFriendRequest for default FriendRequest initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFriendRequestFormGroup(sampleWithNewData);

        const friendRequest = service.getFriendRequest(formGroup) as any;

        expect(friendRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewFriendRequest for empty FriendRequest initial value', () => {
        const formGroup = service.createFriendRequestFormGroup();

        const friendRequest = service.getFriendRequest(formGroup) as any;

        expect(friendRequest).toMatchObject({});
      });

      it('should return IFriendRequest', () => {
        const formGroup = service.createFriendRequestFormGroup(sampleWithRequiredData);

        const friendRequest = service.getFriendRequest(formGroup) as any;

        expect(friendRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFriendRequest should not enable id FormControl', () => {
        const formGroup = service.createFriendRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFriendRequest should disable id FormControl', () => {
        const formGroup = service.createFriendRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
