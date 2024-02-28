import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendsInsightsComponent } from './friends-insights.component';

describe('FriendsInsightsComponent', () => {
  let component: FriendsInsightsComponent;
  let fixture: ComponentFixture<FriendsInsightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FriendsInsightsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FriendsInsightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
