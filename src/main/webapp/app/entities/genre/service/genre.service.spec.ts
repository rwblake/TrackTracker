import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGenre } from '../genre.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../genre.test-samples';

import { GenreService } from './genre.service';

const requireRestSample: IGenre = {
  ...sampleWithRequiredData,
};

describe('Genre Service', () => {
  let service: GenreService;
  let httpMock: HttpTestingController;
  let expectedResult: IGenre | IGenre[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GenreService);
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

    it('should create a Genre', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const genre = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(genre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Genre', () => {
      const genre = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(genre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Genre', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Genre', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Genre', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addGenreToCollectionIfMissing', () => {
      it('should add a Genre to an empty array', () => {
        const genre: IGenre = sampleWithRequiredData;
        expectedResult = service.addGenreToCollectionIfMissing([], genre);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genre);
      });

      it('should not add a Genre to an array that contains it', () => {
        const genre: IGenre = sampleWithRequiredData;
        const genreCollection: IGenre[] = [
          {
            ...genre,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGenreToCollectionIfMissing(genreCollection, genre);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Genre to an array that doesn't contain it", () => {
        const genre: IGenre = sampleWithRequiredData;
        const genreCollection: IGenre[] = [sampleWithPartialData];
        expectedResult = service.addGenreToCollectionIfMissing(genreCollection, genre);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genre);
      });

      it('should add only unique Genre to an array', () => {
        const genreArray: IGenre[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const genreCollection: IGenre[] = [sampleWithRequiredData];
        expectedResult = service.addGenreToCollectionIfMissing(genreCollection, ...genreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const genre: IGenre = sampleWithRequiredData;
        const genre2: IGenre = sampleWithPartialData;
        expectedResult = service.addGenreToCollectionIfMissing([], genre, genre2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(genre);
        expect(expectedResult).toContain(genre2);
      });

      it('should accept null and undefined values', () => {
        const genre: IGenre = sampleWithRequiredData;
        expectedResult = service.addGenreToCollectionIfMissing([], null, genre, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(genre);
      });

      it('should return initial array if no Genre is added', () => {
        const genreCollection: IGenre[] = [sampleWithRequiredData];
        expectedResult = service.addGenreToCollectionIfMissing(genreCollection, undefined, null);
        expect(expectedResult).toEqual(genreCollection);
      });
    });

    describe('compareGenre', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGenre(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGenre(entity1, entity2);
        const compareResult2 = service.compareGenre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGenre(entity1, entity2);
        const compareResult2 = service.compareGenre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGenre(entity1, entity2);
        const compareResult2 = service.compareGenre(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
