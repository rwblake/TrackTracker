import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../feed-card.test-samples';

import { FeedCardFormService } from './feed-card-form.service';

describe('FeedCard Form Service', () => {
  let service: FeedCardFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeedCardFormService);
  });

  describe('Service methods', () => {
    describe('createFeedCardFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFeedCardFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            liked: expect.any(Object),
            feed: expect.any(Object),
            card: expect.any(Object),
          })
        );
      });

      it('passing IFeedCard should create a new form with FormGroup', () => {
        const formGroup = service.createFeedCardFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            liked: expect.any(Object),
            feed: expect.any(Object),
            card: expect.any(Object),
          })
        );
      });
    });

    describe('getFeedCard', () => {
      it('should return NewFeedCard for default FeedCard initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFeedCardFormGroup(sampleWithNewData);

        const feedCard = service.getFeedCard(formGroup) as any;

        expect(feedCard).toMatchObject(sampleWithNewData);
      });

      it('should return NewFeedCard for empty FeedCard initial value', () => {
        const formGroup = service.createFeedCardFormGroup();

        const feedCard = service.getFeedCard(formGroup) as any;

        expect(feedCard).toMatchObject({});
      });

      it('should return IFeedCard', () => {
        const formGroup = service.createFeedCardFormGroup(sampleWithRequiredData);

        const feedCard = service.getFeedCard(formGroup) as any;

        expect(feedCard).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFeedCard should not enable id FormControl', () => {
        const formGroup = service.createFeedCardFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFeedCard should disable id FormControl', () => {
        const formGroup = service.createFeedCardFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
