import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISharingPreference } from '../sharing-preference.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../sharing-preference.test-samples';

import { SharingPreferenceService } from './sharing-preference.service';

const requireRestSample: ISharingPreference = {
  ...sampleWithRequiredData,
};

describe('SharingPreference Service', () => {
  let service: SharingPreferenceService;
  let httpMock: HttpTestingController;
  let expectedResult: ISharingPreference | ISharingPreference[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SharingPreferenceService);
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

    it('should create a SharingPreference', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const sharingPreference = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sharingPreference).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SharingPreference', () => {
      const sharingPreference = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sharingPreference).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SharingPreference', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SharingPreference', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SharingPreference', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSharingPreferenceToCollectionIfMissing', () => {
      it('should add a SharingPreference to an empty array', () => {
        const sharingPreference: ISharingPreference = sampleWithRequiredData;
        expectedResult = service.addSharingPreferenceToCollectionIfMissing([], sharingPreference);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sharingPreference);
      });

      it('should not add a SharingPreference to an array that contains it', () => {
        const sharingPreference: ISharingPreference = sampleWithRequiredData;
        const sharingPreferenceCollection: ISharingPreference[] = [
          {
            ...sharingPreference,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSharingPreferenceToCollectionIfMissing(sharingPreferenceCollection, sharingPreference);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SharingPreference to an array that doesn't contain it", () => {
        const sharingPreference: ISharingPreference = sampleWithRequiredData;
        const sharingPreferenceCollection: ISharingPreference[] = [sampleWithPartialData];
        expectedResult = service.addSharingPreferenceToCollectionIfMissing(sharingPreferenceCollection, sharingPreference);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sharingPreference);
      });

      it('should add only unique SharingPreference to an array', () => {
        const sharingPreferenceArray: ISharingPreference[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sharingPreferenceCollection: ISharingPreference[] = [sampleWithRequiredData];
        expectedResult = service.addSharingPreferenceToCollectionIfMissing(sharingPreferenceCollection, ...sharingPreferenceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sharingPreference: ISharingPreference = sampleWithRequiredData;
        const sharingPreference2: ISharingPreference = sampleWithPartialData;
        expectedResult = service.addSharingPreferenceToCollectionIfMissing([], sharingPreference, sharingPreference2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sharingPreference);
        expect(expectedResult).toContain(sharingPreference2);
      });

      it('should accept null and undefined values', () => {
        const sharingPreference: ISharingPreference = sampleWithRequiredData;
        expectedResult = service.addSharingPreferenceToCollectionIfMissing([], null, sharingPreference, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sharingPreference);
      });

      it('should return initial array if no SharingPreference is added', () => {
        const sharingPreferenceCollection: ISharingPreference[] = [sampleWithRequiredData];
        expectedResult = service.addSharingPreferenceToCollectionIfMissing(sharingPreferenceCollection, undefined, null);
        expect(expectedResult).toEqual(sharingPreferenceCollection);
      });
    });

    describe('compareSharingPreference', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSharingPreference(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSharingPreference(entity1, entity2);
        const compareResult2 = service.compareSharingPreference(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSharingPreference(entity1, entity2);
        const compareResult2 = service.compareSharingPreference(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSharingPreference(entity1, entity2);
        const compareResult2 = service.compareSharingPreference(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
