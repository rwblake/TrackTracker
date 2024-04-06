import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { RegisterService } from './register.service';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { Registration } from './register.model';

describe('RegisterService Service', () => {
  let service: RegisterService;
  let httpMock: HttpTestingController;
  let applicationConfigService: ApplicationConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(RegisterService);
    applicationConfigService = TestBed.inject(ApplicationConfigService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Service methods', () => {
    it('should call register endpoint with correct values', () => {
      // GIVEN
      const firstName = 'firstname';
      const lastName = 'lastname';
      const login = 'abc';
      const email = 'test@test.com';
      const password = 'pass';
      const spotifyID = 'myaccountname';
      const bio = 'This is my bio';
      const spotifyAuthCode = 'code';
      const spotifyAuthState = 'state';
      const registration = new Registration(firstName, lastName, login, email, password, spotifyID, spotifyAuthCode, spotifyAuthState);

      // WHEN
      service.save(registration).subscribe();

      const testRequest = httpMock.expectOne({
        method: 'POST',
        url: applicationConfigService.getEndpointFor('api/register'),
      });

      // THEN
      expect(testRequest.request.body).toEqual({
        firstName,
        lastName,
        login,
        email,
        password,
        spotifyID,
        spotifyAuthCode,
        spotifyAuthState,
      });
    });
  });
});
