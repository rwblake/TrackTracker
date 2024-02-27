import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICardTemplate } from '../card-template.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../card-template.test-samples';

import { CardTemplateService } from './card-template.service';

const requireRestSample: ICardTemplate = {
  ...sampleWithRequiredData,
};

describe('CardTemplate Service', () => {
  let service: CardTemplateService;
  let httpMock: HttpTestingController;
  let expectedResult: ICardTemplate | ICardTemplate[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CardTemplateService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a CardTemplate', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const cardTemplate = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cardTemplate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CardTemplate', () => {
      const cardTemplate = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cardTemplate).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CardTemplate', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CardTemplate', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CardTemplate', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCardTemplateToCollectionIfMissing', () => {
      it('should add a CardTemplate to an empty array', () => {
        const cardTemplate: ICardTemplate = sampleWithRequiredData;
        expectedResult = service.addCardTemplateToCollectionIfMissing([], cardTemplate);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cardTemplate);
      });

      it('should not add a CardTemplate to an array that contains it', () => {
        const cardTemplate: ICardTemplate = sampleWithRequiredData;
        const cardTemplateCollection: ICardTemplate[] = [
          {
            ...cardTemplate,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCardTemplateToCollectionIfMissing(cardTemplateCollection, cardTemplate);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CardTemplate to an array that doesn't contain it", () => {
        const cardTemplate: ICardTemplate = sampleWithRequiredData;
        const cardTemplateCollection: ICardTemplate[] = [sampleWithPartialData];
        expectedResult = service.addCardTemplateToCollectionIfMissing(cardTemplateCollection, cardTemplate);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cardTemplate);
      });

      it('should add only unique CardTemplate to an array', () => {
        const cardTemplateArray: ICardTemplate[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cardTemplateCollection: ICardTemplate[] = [sampleWithRequiredData];
        expectedResult = service.addCardTemplateToCollectionIfMissing(cardTemplateCollection, ...cardTemplateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cardTemplate: ICardTemplate = sampleWithRequiredData;
        const cardTemplate2: ICardTemplate = sampleWithPartialData;
        expectedResult = service.addCardTemplateToCollectionIfMissing([], cardTemplate, cardTemplate2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cardTemplate);
        expect(expectedResult).toContain(cardTemplate2);
      });

      it('should accept null and undefined values', () => {
        const cardTemplate: ICardTemplate = sampleWithRequiredData;
        expectedResult = service.addCardTemplateToCollectionIfMissing([], null, cardTemplate, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cardTemplate);
      });

      it('should return initial array if no CardTemplate is added', () => {
        const cardTemplateCollection: ICardTemplate[] = [sampleWithRequiredData];
        expectedResult = service.addCardTemplateToCollectionIfMissing(cardTemplateCollection, undefined, null);
        expect(expectedResult).toEqual(cardTemplateCollection);
      });
    });

    describe('compareCardTemplate', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCardTemplate(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCardTemplate(entity1, entity2);
        const compareResult2 = service.compareCardTemplate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCardTemplate(entity1, entity2);
        const compareResult2 = service.compareCardTemplate(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCardTemplate(entity1, entity2);
        const compareResult2 = service.compareCardTemplate(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
