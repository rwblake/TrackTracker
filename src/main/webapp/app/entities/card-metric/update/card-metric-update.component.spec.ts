import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CardMetricFormService } from './card-metric-form.service';
import { CardMetricService } from '../service/card-metric.service';
import { ICardMetric } from '../card-metric.model';
import { ICardTemplate } from 'app/entities/card-template/card-template.model';
import { CardTemplateService } from 'app/entities/card-template/service/card-template.service';

import { CardMetricUpdateComponent } from './card-metric-update.component';

describe('CardMetric Management Update Component', () => {
  let comp: CardMetricUpdateComponent;
  let fixture: ComponentFixture<CardMetricUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cardMetricFormService: CardMetricFormService;
  let cardMetricService: CardMetricService;
  let cardTemplateService: CardTemplateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CardMetricUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CardMetricUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CardMetricUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cardMetricFormService = TestBed.inject(CardMetricFormService);
    cardMetricService = TestBed.inject(CardMetricService);
    cardTemplateService = TestBed.inject(CardTemplateService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call CardTemplate query and add missing value', () => {
      const cardMetric: ICardMetric = { id: 456 };
      const cardTemplate: ICardTemplate = { id: 66945 };
      cardMetric.cardTemplate = cardTemplate;

      const cardTemplateCollection: ICardTemplate[] = [{ id: 99805 }];
      jest.spyOn(cardTemplateService, 'query').mockReturnValue(of(new HttpResponse({ body: cardTemplateCollection })));
      const additionalCardTemplates = [cardTemplate];
      const expectedCollection: ICardTemplate[] = [...additionalCardTemplates, ...cardTemplateCollection];
      jest.spyOn(cardTemplateService, 'addCardTemplateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cardMetric });
      comp.ngOnInit();

      expect(cardTemplateService.query).toHaveBeenCalled();
      expect(cardTemplateService.addCardTemplateToCollectionIfMissing).toHaveBeenCalledWith(
        cardTemplateCollection,
        ...additionalCardTemplates.map(expect.objectContaining)
      );
      expect(comp.cardTemplatesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const cardMetric: ICardMetric = { id: 456 };
      const cardTemplate: ICardTemplate = { id: 96993 };
      cardMetric.cardTemplate = cardTemplate;

      activatedRoute.data = of({ cardMetric });
      comp.ngOnInit();

      expect(comp.cardTemplatesSharedCollection).toContain(cardTemplate);
      expect(comp.cardMetric).toEqual(cardMetric);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardMetric>>();
      const cardMetric = { id: 123 };
      jest.spyOn(cardMetricFormService, 'getCardMetric').mockReturnValue(cardMetric);
      jest.spyOn(cardMetricService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardMetric });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cardMetric }));
      saveSubject.complete();

      // THEN
      expect(cardMetricFormService.getCardMetric).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cardMetricService.update).toHaveBeenCalledWith(expect.objectContaining(cardMetric));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardMetric>>();
      const cardMetric = { id: 123 };
      jest.spyOn(cardMetricFormService, 'getCardMetric').mockReturnValue({ id: null });
      jest.spyOn(cardMetricService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardMetric: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cardMetric }));
      saveSubject.complete();

      // THEN
      expect(cardMetricFormService.getCardMetric).toHaveBeenCalled();
      expect(cardMetricService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICardMetric>>();
      const cardMetric = { id: 123 };
      jest.spyOn(cardMetricService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cardMetric });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cardMetricService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCardTemplate', () => {
      it('Should forward to cardTemplateService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(cardTemplateService, 'compareCardTemplate');
        comp.compareCardTemplate(entity, entity2);
        expect(cardTemplateService.compareCardTemplate).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
