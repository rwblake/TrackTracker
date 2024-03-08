import { TestBed } from '@angular/core/testing';

import { FriendsInsightsService } from './friends-insights.service';

describe('FriendsInsightsService', () => {
  let service: FriendsInsightsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FriendsInsightsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
