import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { FeedCardFormService, FeedCardFormGroup } from './feed-card-form.service';
import { IFeedCard } from '../feed-card.model';
import { FeedCardService } from '../service/feed-card.service';
import { IFeed } from 'app/entities/feed/feed.model';
import { FeedService } from 'app/entities/feed/service/feed.service';
import { ICard } from 'app/entities/card/card.model';
import { CardService } from 'app/entities/card/service/card.service';

@Component({
  selector: 'jhi-feed-card-update',
  templateUrl: './feed-card-update.component.html',
})
export class FeedCardUpdateComponent implements OnInit {
  isSaving = false;
  feedCard: IFeedCard | null = null;

  feedsSharedCollection: IFeed[] = [];
  cardsSharedCollection: ICard[] = [];

  editForm: FeedCardFormGroup = this.feedCardFormService.createFeedCardFormGroup();

  constructor(
    protected feedCardService: FeedCardService,
    protected feedCardFormService: FeedCardFormService,
    protected feedService: FeedService,
    protected cardService: CardService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareFeed = (o1: IFeed | null, o2: IFeed | null): boolean => this.feedService.compareFeed(o1, o2);

  compareCard = (o1: ICard | null, o2: ICard | null): boolean => this.cardService.compareCard(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ feedCard }) => {
      this.feedCard = feedCard;
      if (feedCard) {
        this.updateForm(feedCard);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const feedCard = this.feedCardFormService.getFeedCard(this.editForm);
    if (feedCard.id !== null) {
      this.subscribeToSaveResponse(this.feedCardService.update(feedCard));
    } else {
      this.subscribeToSaveResponse(this.feedCardService.create(feedCard));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFeedCard>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(feedCard: IFeedCard): void {
    this.feedCard = feedCard;
    this.feedCardFormService.resetForm(this.editForm, feedCard);

    this.feedsSharedCollection = this.feedService.addFeedToCollectionIfMissing<IFeed>(this.feedsSharedCollection, feedCard.feed);
    this.cardsSharedCollection = this.cardService.addCardToCollectionIfMissing<ICard>(this.cardsSharedCollection, feedCard.card);
  }

  protected loadRelationshipsOptions(): void {
    this.feedService
      .query()
      .pipe(map((res: HttpResponse<IFeed[]>) => res.body ?? []))
      .pipe(map((feeds: IFeed[]) => this.feedService.addFeedToCollectionIfMissing<IFeed>(feeds, this.feedCard?.feed)))
      .subscribe((feeds: IFeed[]) => (this.feedsSharedCollection = feeds));

    this.cardService
      .query()
      .pipe(map((res: HttpResponse<ICard[]>) => res.body ?? []))
      .pipe(map((cards: ICard[]) => this.cardService.addCardToCollectionIfMissing<ICard>(cards, this.feedCard?.card)))
      .subscribe((cards: ICard[]) => (this.cardsSharedCollection = cards));
  }
}
