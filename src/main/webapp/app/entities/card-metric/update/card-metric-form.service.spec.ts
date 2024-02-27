import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../card-metric.test-samples';

import { CardMetricFormService } from './card-metric-form.service';

describe('CardMetric Form Service', () => {
  let service: CardMetricFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CardMetricFormService);
  });

  describe('Service methods', () => {
    describe('createCardMetricFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCardMetricFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            metric: expect.any(Object),
            cardTemplate: expect.any(Object),
          })
        );
      });

      it('passing ICardMetric should create a new form with FormGroup', () => {
        const formGroup = service.createCardMetricFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            metric: expect.any(Object),
            cardTemplate: expect.any(Object),
          })
        );
      });
    });

    describe('getCardMetric', () => {
      it('should return NewCardMetric for default CardMetric initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCardMetricFormGroup(sampleWithNewData);

        const cardMetric = service.getCardMetric(formGroup) as any;

        expect(cardMetric).toMatchObject(sampleWithNewData);
      });

      it('should return NewCardMetric for empty CardMetric initial value', () => {
        const formGroup = service.createCardMetricFormGroup();

        const cardMetric = service.getCardMetric(formGroup) as any;

        expect(cardMetric).toMatchObject({});
      });

      it('should return ICardMetric', () => {
        const formGroup = service.createCardMetricFormGroup(sampleWithRequiredData);

        const cardMetric = service.getCardMetric(formGroup) as any;

        expect(cardMetric).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICardMetric should not enable id FormControl', () => {
        const formGroup = service.createCardMetricFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCardMetric should disable id FormControl', () => {
        const formGroup = service.createCardMetricFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
