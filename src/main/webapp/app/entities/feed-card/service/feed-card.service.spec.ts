import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFeedCard } from '../feed-card.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../feed-card.test-samples';

import { FeedCardService } from './feed-card.service';

const requireRestSample: IFeedCard = {
  ...sampleWithRequiredData,
};

describe('FeedCard Service', () => {
  let service: FeedCardService;
  let httpMock: HttpTestingController;
  let expectedResult: IFeedCard | IFeedCard[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FeedCardService);
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

    it('should create a FeedCard', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const feedCard = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(feedCard).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FeedCard', () => {
      const feedCard = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(feedCard).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FeedCard', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FeedCard', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FeedCard', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFeedCardToCollectionIfMissing', () => {
      it('should add a FeedCard to an empty array', () => {
        const feedCard: IFeedCard = sampleWithRequiredData;
        expectedResult = service.addFeedCardToCollectionIfMissing([], feedCard);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(feedCard);
      });

      it('should not add a FeedCard to an array that contains it', () => {
        const feedCard: IFeedCard = sampleWithRequiredData;
        const feedCardCollection: IFeedCard[] = [
          {
            ...feedCard,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFeedCardToCollectionIfMissing(feedCardCollection, feedCard);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FeedCard to an array that doesn't contain it", () => {
        const feedCard: IFeedCard = sampleWithRequiredData;
        const feedCardCollection: IFeedCard[] = [sampleWithPartialData];
        expectedResult = service.addFeedCardToCollectionIfMissing(feedCardCollection, feedCard);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(feedCard);
      });

      it('should add only unique FeedCard to an array', () => {
        const feedCardArray: IFeedCard[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const feedCardCollection: IFeedCard[] = [sampleWithRequiredData];
        expectedResult = service.addFeedCardToCollectionIfMissing(feedCardCollection, ...feedCardArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const feedCard: IFeedCard = sampleWithRequiredData;
        const feedCard2: IFeedCard = sampleWithPartialData;
        expectedResult = service.addFeedCardToCollectionIfMissing([], feedCard, feedCard2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(feedCard);
        expect(expectedResult).toContain(feedCard2);
      });

      it('should accept null and undefined values', () => {
        const feedCard: IFeedCard = sampleWithRequiredData;
        expectedResult = service.addFeedCardToCollectionIfMissing([], null, feedCard, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(feedCard);
      });

      it('should return initial array if no FeedCard is added', () => {
        const feedCardCollection: IFeedCard[] = [sampleWithRequiredData];
        expectedResult = service.addFeedCardToCollectionIfMissing(feedCardCollection, undefined, null);
        expect(expectedResult).toEqual(feedCardCollection);
      });
    });

    describe('compareFeedCard', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFeedCard(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFeedCard(entity1, entity2);
        const compareResult2 = service.compareFeedCard(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFeedCard(entity1, entity2);
        const compareResult2 = service.compareFeedCard(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFeedCard(entity1, entity2);
        const compareResult2 = service.compareFeedCard(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
