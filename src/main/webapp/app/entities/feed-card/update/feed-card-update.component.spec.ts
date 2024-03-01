import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FeedCardFormService } from './feed-card-form.service';
import { FeedCardService } from '../service/feed-card.service';
import { IFeedCard } from '../feed-card.model';
import { IFeed } from 'app/entities/feed/feed.model';
import { FeedService } from 'app/entities/feed/service/feed.service';
import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';

import { FeedCardUpdateComponent } from './feed-card-update.component';

describe('FeedCard Management Update Component', () => {
  let comp: FeedCardUpdateComponent;
  let fixture: ComponentFixture<FeedCardUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let feedCardFormService: FeedCardFormService;
  let feedCardService: FeedCardService;
  let feedService: FeedService;
  let cardService: CardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FeedCardUpdateComponent],
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
      .overrideTemplate(FeedCardUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FeedCardUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    feedCardFormService = TestBed.inject(FeedCardFormService);
    feedCardService = TestBed.inject(FeedCardService);
    feedService = TestBed.inject(FeedService);
    cardService = TestBed.inject(CardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Feed query and add missing value', () => {
      const feedCard: IFeedCard = { id: 456 };
      const feed: IFeed = { id: 94150 };
      feedCard.feed = feed;

      const feedCollection: IFeed[] = [{ id: 69719 }];
      jest.spyOn(feedService, 'query').mockReturnValue(of(new HttpResponse({ body: feedCollection })));
      const additionalFeeds = [feed];
      const expectedCollection: IFeed[] = [...additionalFeeds, ...feedCollection];
      jest.spyOn(feedService, 'addFeedToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ feedCard });
      comp.ngOnInit();

      expect(feedService.query).toHaveBeenCalled();
      expect(feedService.addFeedToCollectionIfMissing).toHaveBeenCalledWith(
        feedCollection,
        ...additionalFeeds.map(expect.objectContaining)
      );
      expect(comp.feedsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Card query and add missing value', () => {
      const feedCard: IFeedCard = { id: 456 };
      const card: ICard = { id: 69717 };
      feedCard.card = card;

      const cardCollection: ICard[] = [{ id: 96315 }];
      jest.spyOn(cardService, 'query').mockReturnValue(of(new HttpResponse({ body: cardCollection })));
      const additionalCards = [card];
      const expectedCollection: ICard[] = [...additionalCards, ...cardCollection];
      jest.spyOn(cardService, 'addCardToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ feedCard });
      comp.ngOnInit();

      expect(cardService.query).toHaveBeenCalled();
      expect(cardService.addCardToCollectionIfMissing).toHaveBeenCalledWith(
        cardCollection,
        ...additionalCards.map(expect.objectContaining)
      );
      expect(comp.cardsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const feedCard: IFeedCard = { id: 456 };
      const feed: IFeed = { id: 16564 };
      feedCard.feed = feed;
      const card: ICard = { id: 56180 };
      feedCard.card = card;

      activatedRoute.data = of({ feedCard });
      comp.ngOnInit();

      expect(comp.feedsSharedCollection).toContain(feed);
      expect(comp.cardsSharedCollection).toContain(card);
      expect(comp.feedCard).toEqual(feedCard);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFeedCard>>();
      const feedCard = { id: 123 };
      jest.spyOn(feedCardFormService, 'getFeedCard').mockReturnValue(feedCard);
      jest.spyOn(feedCardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feedCard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: feedCard }));
      saveSubject.complete();

      // THEN
      expect(feedCardFormService.getFeedCard).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(feedCardService.update).toHaveBeenCalledWith(expect.objectContaining(feedCard));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFeedCard>>();
      const feedCard = { id: 123 };
      jest.spyOn(feedCardFormService, 'getFeedCard').mockReturnValue({ id: null });
      jest.spyOn(feedCardService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feedCard: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: feedCard }));
      saveSubject.complete();

      // THEN
      expect(feedCardFormService.getFeedCard).toHaveBeenCalled();
      expect(feedCardService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFeedCard>>();
      const feedCard = { id: 123 };
      jest.spyOn(feedCardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feedCard });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(feedCardService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFeed', () => {
      it('Should forward to feedService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(feedService, 'compareFeed');
        comp.compareFeed(entity, entity2);
        expect(feedService.compareFeed).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCard', () => {
      it('Should forward to cardService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(cardService, 'compareCard');
        comp.compareCard(entity, entity2);
        expect(cardService.compareCard).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
