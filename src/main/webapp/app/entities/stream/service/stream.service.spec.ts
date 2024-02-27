import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IStream } from '../stream.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../stream.test-samples';

import { StreamService, RestStream } from './stream.service';

const requireRestSample: RestStream = {
  ...sampleWithRequiredData,
  playedAt: sampleWithRequiredData.playedAt?.toJSON(),
};

describe('Stream Service', () => {
  let service: StreamService;
  let httpMock: HttpTestingController;
  let expectedResult: IStream | IStream[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StreamService);
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

    it('should create a Stream', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const stream = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(stream).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Stream', () => {
      const stream = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(stream).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Stream', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Stream', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Stream', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStreamToCollectionIfMissing', () => {
      it('should add a Stream to an empty array', () => {
        const stream: IStream = sampleWithRequiredData;
        expectedResult = service.addStreamToCollectionIfMissing([], stream);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stream);
      });

      it('should not add a Stream to an array that contains it', () => {
        const stream: IStream = sampleWithRequiredData;
        const streamCollection: IStream[] = [
          {
            ...stream,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStreamToCollectionIfMissing(streamCollection, stream);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Stream to an array that doesn't contain it", () => {
        const stream: IStream = sampleWithRequiredData;
        const streamCollection: IStream[] = [sampleWithPartialData];
        expectedResult = service.addStreamToCollectionIfMissing(streamCollection, stream);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stream);
      });

      it('should add only unique Stream to an array', () => {
        const streamArray: IStream[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const streamCollection: IStream[] = [sampleWithRequiredData];
        expectedResult = service.addStreamToCollectionIfMissing(streamCollection, ...streamArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stream: IStream = sampleWithRequiredData;
        const stream2: IStream = sampleWithPartialData;
        expectedResult = service.addStreamToCollectionIfMissing([], stream, stream2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stream);
        expect(expectedResult).toContain(stream2);
      });

      it('should accept null and undefined values', () => {
        const stream: IStream = sampleWithRequiredData;
        expectedResult = service.addStreamToCollectionIfMissing([], null, stream, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stream);
      });

      it('should return initial array if no Stream is added', () => {
        const streamCollection: IStream[] = [sampleWithRequiredData];
        expectedResult = service.addStreamToCollectionIfMissing(streamCollection, undefined, null);
        expect(expectedResult).toEqual(streamCollection);
      });
    });

    describe('compareStream', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStream(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareStream(entity1, entity2);
        const compareResult2 = service.compareStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareStream(entity1, entity2);
        const compareResult2 = service.compareStream(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareStream(entity1, entity2);
        const compareResult2 = service.compareStream(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
