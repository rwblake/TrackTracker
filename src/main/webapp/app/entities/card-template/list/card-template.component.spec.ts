import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CardTemplateService } from '../service/card-template.service';

import { CardTemplateComponent } from './card-template.component';

describe('CardTemplate Management Component', () => {
  let comp: CardTemplateComponent;
  let fixture: ComponentFixture<CardTemplateComponent>;
  let service: CardTemplateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'card-template', component: CardTemplateComponent }]), HttpClientTestingModule],
      declarations: [CardTemplateComponent],
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
      .overrideTemplate(CardTemplateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CardTemplateComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CardTemplateService);

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
    expect(comp.cardTemplates?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to cardTemplateService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCardTemplateIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCardTemplateIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
