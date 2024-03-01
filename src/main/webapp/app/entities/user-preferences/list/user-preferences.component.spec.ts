import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserPreferencesService } from '../service/user-preferences.service';

import { UserPreferencesComponent } from './user-preferences.component';

describe('UserPreferences Management Component', () => {
  let comp: UserPreferencesComponent;
  let fixture: ComponentFixture<UserPreferencesComponent>;
  let service: UserPreferencesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'user-preferences', component: UserPreferencesComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [UserPreferencesComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(UserPreferencesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserPreferencesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UserPreferencesService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.userPreferences?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to userPreferencesService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getUserPreferencesIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getUserPreferencesIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
