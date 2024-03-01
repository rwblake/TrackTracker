import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../card-template.test-samples';

import { CardTemplateFormService } from './card-template-form.service';

describe('CardTemplate Form Service', () => {
  let service: CardTemplateFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CardTemplateFormService);
  });

  describe('Service methods', () => {
    describe('createCardTemplateFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCardTemplateFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            color: expect.any(Object),
            layout: expect.any(Object),
            name: expect.any(Object),
            font: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });

      it('passing ICardTemplate should create a new form with FormGroup', () => {
        const formGroup = service.createCardTemplateFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            color: expect.any(Object),
            layout: expect.any(Object),
            name: expect.any(Object),
            font: expect.any(Object),
            appUser: expect.any(Object),
          })
        );
      });
    });

    describe('getCardTemplate', () => {
      it('should return NewCardTemplate for default CardTemplate initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCardTemplateFormGroup(sampleWithNewData);

        const cardTemplate = service.getCardTemplate(formGroup) as any;

        expect(cardTemplate).toMatchObject(sampleWithNewData);
      });

      it('should return NewCardTemplate for empty CardTemplate initial value', () => {
        const formGroup = service.createCardTemplateFormGroup();

        const cardTemplate = service.getCardTemplate(formGroup) as any;

        expect(cardTemplate).toMatchObject({});
      });

      it('should return ICardTemplate', () => {
        const formGroup = service.createCardTemplateFormGroup(sampleWithRequiredData);

        const cardTemplate = service.getCardTemplate(formGroup) as any;

        expect(cardTemplate).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICardTemplate should not enable id FormControl', () => {
        const formGroup = service.createCardTemplateFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCardTemplate should disable id FormControl', () => {
        const formGroup = service.createCardTemplateFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
