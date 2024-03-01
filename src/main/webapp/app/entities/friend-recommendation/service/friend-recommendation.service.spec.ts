import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IFriendRecommendation } from '../friend-recommendation.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../friend-recommendation.test-samples';

import { FriendRecommendationService, RestFriendRecommendation } from './friend-recommendation.service';

const requireRestSample: RestFriendRecommendation = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.format(DATE_FORMAT),
};

describe('FriendRecommendation Service', () => {
  let service: FriendRecommendationService;
  let httpMock: HttpTestingController;
  let expectedResult: IFriendRecommendation | IFriendRecommendation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FriendRecommendationService);
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

    it('should create a FriendRecommendation', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const friendRecommendation = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(friendRecommendation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FriendRecommendation', () => {
      const friendRecommendation = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(friendRecommendation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FriendRecommendation', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FriendRecommendation', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FriendRecommendation', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFriendRecommendationToCollectionIfMissing', () => {
      it('should add a FriendRecommendation to an empty array', () => {
        const friendRecommendation: IFriendRecommendation = sampleWithRequiredData;
        expectedResult = service.addFriendRecommendationToCollectionIfMissing([], friendRecommendation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(friendRecommendation);
      });

      it('should not add a FriendRecommendation to an array that contains it', () => {
        const friendRecommendation: IFriendRecommendation = sampleWithRequiredData;
        const friendRecommendationCollection: IFriendRecommendation[] = [
          {
            ...friendRecommendation,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFriendRecommendationToCollectionIfMissing(friendRecommendationCollection, friendRecommendation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FriendRecommendation to an array that doesn't contain it", () => {
        const friendRecommendation: IFriendRecommendation = sampleWithRequiredData;
        const friendRecommendationCollection: IFriendRecommendation[] = [sampleWithPartialData];
        expectedResult = service.addFriendRecommendationToCollectionIfMissing(friendRecommendationCollection, friendRecommendation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(friendRecommendation);
      });

      it('should add only unique FriendRecommendation to an array', () => {
        const friendRecommendationArray: IFriendRecommendation[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const friendRecommendationCollection: IFriendRecommendation[] = [sampleWithRequiredData];
        expectedResult = service.addFriendRecommendationToCollectionIfMissing(friendRecommendationCollection, ...friendRecommendationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const friendRecommendation: IFriendRecommendation = sampleWithRequiredData;
        const friendRecommendation2: IFriendRecommendation = sampleWithPartialData;
        expectedResult = service.addFriendRecommendationToCollectionIfMissing([], friendRecommendation, friendRecommendation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(friendRecommendation);
        expect(expectedResult).toContain(friendRecommendation2);
      });

      it('should accept null and undefined values', () => {
        const friendRecommendation: IFriendRecommendation = sampleWithRequiredData;
        expectedResult = service.addFriendRecommendationToCollectionIfMissing([], null, friendRecommendation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(friendRecommendation);
      });

      it('should return initial array if no FriendRecommendation is added', () => {
        const friendRecommendationCollection: IFriendRecommendation[] = [sampleWithRequiredData];
        expectedResult = service.addFriendRecommendationToCollectionIfMissing(friendRecommendationCollection, undefined, null);
        expect(expectedResult).toEqual(friendRecommendationCollection);
      });
    });

    describe('compareFriendRecommendation', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFriendRecommendation(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFriendRecommendation(entity1, entity2);
        const compareResult2 = service.compareFriendRecommendation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFriendRecommendation(entity1, entity2);
        const compareResult2 = service.compareFriendRecommendation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFriendRecommendation(entity1, entity2);
        const compareResult2 = service.compareFriendRecommendation(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
