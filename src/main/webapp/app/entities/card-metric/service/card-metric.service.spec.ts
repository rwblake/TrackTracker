import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICardMetric } from '../card-metric.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../card-metric.test-samples';

import { CardMetricService } from './card-metric.service';

const requireRestSample: ICardMetric = {
  ...sampleWithRequiredData,
};

describe('CardMetric Service', () => {
  let service: CardMetricService;
  let httpMock: HttpTestingController;
  let expectedResult: ICardMetric | ICardMetric[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CardMetricService);
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

    it('should create a CardMetric', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const cardMetric = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cardMetric).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CardMetric', () => {
      const cardMetric = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cardMetric).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CardMetric', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CardMetric', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CardMetric', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCardMetricToCollectionIfMissing', () => {
      it('should add a CardMetric to an empty array', () => {
        const cardMetric: ICardMetric = sampleWithRequiredData;
        expectedResult = service.addCardMetricToCollectionIfMissing([], cardMetric);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cardMetric);
      });

      it('should not add a CardMetric to an array that contains it', () => {
        const cardMetric: ICardMetric = sampleWithRequiredData;
        const cardMetricCollection: ICardMetric[] = [
          {
            ...cardMetric,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCardMetricToCollectionIfMissing(cardMetricCollection, cardMetric);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CardMetric to an array that doesn't contain it", () => {
        const cardMetric: ICardMetric = sampleWithRequiredData;
        const cardMetricCollection: ICardMetric[] = [sampleWithPartialData];
        expectedResult = service.addCardMetricToCollectionIfMissing(cardMetricCollection, cardMetric);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cardMetric);
      });

      it('should add only unique CardMetric to an array', () => {
        const cardMetricArray: ICardMetric[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cardMetricCollection: ICardMetric[] = [sampleWithRequiredData];
        expectedResult = service.addCardMetricToCollectionIfMissing(cardMetricCollection, ...cardMetricArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cardMetric: ICardMetric = sampleWithRequiredData;
        const cardMetric2: ICardMetric = sampleWithPartialData;
        expectedResult = service.addCardMetricToCollectionIfMissing([], cardMetric, cardMetric2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cardMetric);
        expect(expectedResult).toContain(cardMetric2);
      });

      it('should accept null and undefined values', () => {
        const cardMetric: ICardMetric = sampleWithRequiredData;
        expectedResult = service.addCardMetricToCollectionIfMissing([], null, cardMetric, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cardMetric);
      });

      it('should return initial array if no CardMetric is added', () => {
        const cardMetricCollection: ICardMetric[] = [sampleWithRequiredData];
        expectedResult = service.addCardMetricToCollectionIfMissing(cardMetricCollection, undefined, null);
        expect(expectedResult).toEqual(cardMetricCollection);
      });
    });

    describe('compareCardMetric', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCardMetric(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCardMetric(entity1, entity2);
        const compareResult2 = service.compareCardMetric(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCardMetric(entity1, entity2);
        const compareResult2 = service.compareCardMetric(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCardMetric(entity1, entity2);
        const compareResult2 = service.compareCardMetric(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
