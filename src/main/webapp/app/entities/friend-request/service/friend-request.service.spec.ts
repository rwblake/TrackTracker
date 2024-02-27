import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFriendRequest } from '../friend-request.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../friend-request.test-samples';

import { FriendRequestService, RestFriendRequest } from './friend-request.service';

const requireRestSample: RestFriendRequest = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('FriendRequest Service', () => {
  let service: FriendRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: IFriendRequest | IFriendRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FriendRequestService);
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

    it('should create a FriendRequest', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const friendRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(friendRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FriendRequest', () => {
      const friendRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(friendRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FriendRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FriendRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FriendRequest', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFriendRequestToCollectionIfMissing', () => {
      it('should add a FriendRequest to an empty array', () => {
        const friendRequest: IFriendRequest = sampleWithRequiredData;
        expectedResult = service.addFriendRequestToCollectionIfMissing([], friendRequest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(friendRequest);
      });

      it('should not add a FriendRequest to an array that contains it', () => {
        const friendRequest: IFriendRequest = sampleWithRequiredData;
        const friendRequestCollection: IFriendRequest[] = [
          {
            ...friendRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFriendRequestToCollectionIfMissing(friendRequestCollection, friendRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FriendRequest to an array that doesn't contain it", () => {
        const friendRequest: IFriendRequest = sampleWithRequiredData;
        const friendRequestCollection: IFriendRequest[] = [sampleWithPartialData];
        expectedResult = service.addFriendRequestToCollectionIfMissing(friendRequestCollection, friendRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(friendRequest);
      });

      it('should add only unique FriendRequest to an array', () => {
        const friendRequestArray: IFriendRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const friendRequestCollection: IFriendRequest[] = [sampleWithRequiredData];
        expectedResult = service.addFriendRequestToCollectionIfMissing(friendRequestCollection, ...friendRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const friendRequest: IFriendRequest = sampleWithRequiredData;
        const friendRequest2: IFriendRequest = sampleWithPartialData;
        expectedResult = service.addFriendRequestToCollectionIfMissing([], friendRequest, friendRequest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(friendRequest);
        expect(expectedResult).toContain(friendRequest2);
      });

      it('should accept null and undefined values', () => {
        const friendRequest: IFriendRequest = sampleWithRequiredData;
        expectedResult = service.addFriendRequestToCollectionIfMissing([], null, friendRequest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(friendRequest);
      });

      it('should return initial array if no FriendRequest is added', () => {
        const friendRequestCollection: IFriendRequest[] = [sampleWithRequiredData];
        expectedResult = service.addFriendRequestToCollectionIfMissing(friendRequestCollection, undefined, null);
        expect(expectedResult).toEqual(friendRequestCollection);
      });
    });

    describe('compareFriendRequest', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFriendRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFriendRequest(entity1, entity2);
        const compareResult2 = service.compareFriendRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFriendRequest(entity1, entity2);
        const compareResult2 = service.compareFriendRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFriendRequest(entity1, entity2);
        const compareResult2 = service.compareFriendRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
