import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { FeedFormService, FeedFormGroup } from './feed-form.service';
import { IFeed } from '../feed.model';
import { FeedService } from '../service/feed.service';

@Component({
  selector: 'jhi-feed-update',
  templateUrl: './feed-update.component.html',
})
export class FeedUpdateComponent implements OnInit {
  isSaving = false;
  feed: IFeed | null = null;

  editForm: FeedFormGroup = this.feedFormService.createFeedFormGroup();

  constructor(protected feedService: FeedService, protected feedFormService: FeedFormService, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ feed }) => {
      this.feed = feed;
      if (feed) {
        this.updateForm(feed);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const feed = this.feedFormService.getFeed(this.editForm);
    if (feed.id !== null) {
      this.subscribeToSaveResponse(this.feedService.update(feed));
    } else {
      this.subscribeToSaveResponse(this.feedService.create(feed));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFeed>>): void {
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

  protected updateForm(feed: IFeed): void {
    this.feed = feed;
    this.feedFormService.resetForm(this.editForm, feed);
  }
}
