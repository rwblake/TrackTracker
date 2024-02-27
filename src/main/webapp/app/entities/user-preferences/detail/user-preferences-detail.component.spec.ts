import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserPreferencesDetailComponent } from './user-preferences-detail.component';

describe('UserPreferences Management Detail Component', () => {
  let comp: UserPreferencesDetailComponent;
  let fixture: ComponentFixture<UserPreferencesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserPreferencesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userPreferences: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserPreferencesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserPreferencesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userPreferences on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userPreferences).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
