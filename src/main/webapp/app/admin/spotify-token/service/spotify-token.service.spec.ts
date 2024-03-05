import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISpotifyToken } from '../spotify-token.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../spotify-token.test-samples';

import { SpotifyTokenService, RestSpotifyToken } from './spotify-token.service';

const requireRestSample: RestSpotifyToken = {
  ...sampleWithRequiredData,
  expires: sampleWithRequiredData.expires?.toJSON(),
};

describe('SpotifyToken Service', () => {
  let service: SpotifyTokenService;
  let httpMock: HttpTestingController;
  let expectedResult: ISpotifyToken | ISpotifyToken[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SpotifyTokenService);
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

    it('should create a SpotifyToken', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const spotifyToken = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(spotifyToken).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SpotifyToken', () => {
      const spotifyToken = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(spotifyToken).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SpotifyToken', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SpotifyToken', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SpotifyToken', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSpotifyTokenToCollectionIfMissing', () => {
      it('should add a SpotifyToken to an empty array', () => {
        const spotifyToken: ISpotifyToken = sampleWithRequiredData;
        expectedResult = service.addSpotifyTokenToCollectionIfMissing([], spotifyToken);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spotifyToken);
      });

      it('should not add a SpotifyToken to an array that contains it', () => {
        const spotifyToken: ISpotifyToken = sampleWithRequiredData;
        const spotifyTokenCollection: ISpotifyToken[] = [
          {
            ...spotifyToken,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSpotifyTokenToCollectionIfMissing(spotifyTokenCollection, spotifyToken);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SpotifyToken to an array that doesn't contain it", () => {
        const spotifyToken: ISpotifyToken = sampleWithRequiredData;
        const spotifyTokenCollection: ISpotifyToken[] = [sampleWithPartialData];
        expectedResult = service.addSpotifyTokenToCollectionIfMissing(spotifyTokenCollection, spotifyToken);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spotifyToken);
      });

      it('should add only unique SpotifyToken to an array', () => {
        const spotifyTokenArray: ISpotifyToken[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const spotifyTokenCollection: ISpotifyToken[] = [sampleWithRequiredData];
        expectedResult = service.addSpotifyTokenToCollectionIfMissing(spotifyTokenCollection, ...spotifyTokenArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const spotifyToken: ISpotifyToken = sampleWithRequiredData;
        const spotifyToken2: ISpotifyToken = sampleWithPartialData;
        expectedResult = service.addSpotifyTokenToCollectionIfMissing([], spotifyToken, spotifyToken2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spotifyToken);
        expect(expectedResult).toContain(spotifyToken2);
      });

      it('should accept null and undefined values', () => {
        const spotifyToken: ISpotifyToken = sampleWithRequiredData;
        expectedResult = service.addSpotifyTokenToCollectionIfMissing([], null, spotifyToken, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spotifyToken);
      });

      it('should return initial array if no SpotifyToken is added', () => {
        const spotifyTokenCollection: ISpotifyToken[] = [sampleWithRequiredData];
        expectedResult = service.addSpotifyTokenToCollectionIfMissing(spotifyTokenCollection, undefined, null);
        expect(expectedResult).toEqual(spotifyTokenCollection);
      });
    });

    describe('compareSpotifyToken', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSpotifyToken(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSpotifyToken(entity1, entity2);
        const compareResult2 = service.compareSpotifyToken(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSpotifyToken(entity1, entity2);
        const compareResult2 = service.compareSpotifyToken(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSpotifyToken(entity1, entity2);
        const compareResult2 = service.compareSpotifyToken(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
