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
      const login = 'abc';
      const email = 'test@test.com';
      const password = 'pass';
      const spotifyID = 'myaccountname';
      const bio = 'This is my bio';
      const credentials = {
        accessToken: 'token',
        tokenType: 'tokentype',
        scope: 'scope1, scope2',
        expiresIn: 0,
        refreshToken: 'refresh',
      };
      const langKey = 'FR';
      const registration = new Registration(login, email, password, spotifyID, bio, credentials, langKey);

      // WHEN
      service.save(registration).subscribe();

      const testRequest = httpMock.expectOne({
        method: 'POST',
        url: applicationConfigService.getEndpointFor('api/register'),
      });

      // THEN
      expect(testRequest.request.body).toEqual({ email, langKey, login, password, spotifyID, bio, credentials });
    });
  });
});
