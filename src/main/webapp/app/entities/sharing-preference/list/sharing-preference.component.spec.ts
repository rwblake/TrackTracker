import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SharingPreferenceService } from '../service/sharing-preference.service';

import { SharingPreferenceComponent } from './sharing-preference.component';

describe('SharingPreference Management Component', () => {
  let comp: SharingPreferenceComponent;
  let fixture: ComponentFixture<SharingPreferenceComponent>;
  let service: SharingPreferenceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'sharing-preference', component: SharingPreferenceComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [SharingPreferenceComponent],
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
      .overrideTemplate(SharingPreferenceComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SharingPreferenceComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SharingPreferenceService);

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
    expect(comp.sharingPreferences?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sharingPreferenceService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSharingPreferenceIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSharingPreferenceIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
