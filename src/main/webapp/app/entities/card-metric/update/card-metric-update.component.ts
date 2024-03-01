import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CardMetricFormService, CardMetricFormGroup } from './card-metric-form.service';
import { ICardMetric } from '../card-metric.model';
import { CardMetricService } from '../service/card-metric.service';
import { ICardTemplate } from 'app/entities/card-template/card-template.model';
import { CardTemplateService } from 'app/entities/card-template/service/card-template.service';
import { CardType } from 'app/entities/enumerations/card-type.model';

@Component({
  selector: 'jhi-card-metric-update',
  templateUrl: './card-metric-update.component.html',
})
export class CardMetricUpdateComponent implements OnInit {
  isSaving = false;
  cardMetric: ICardMetric | null = null;
  cardTypeValues = Object.keys(CardType);

  cardTemplatesSharedCollection: ICardTemplate[] = [];

  editForm: CardMetricFormGroup = this.cardMetricFormService.createCardMetricFormGroup();

  constructor(
    protected cardMetricService: CardMetricService,
    protected cardMetricFormService: CardMetricFormService,
    protected cardTemplateService: CardTemplateService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCardTemplate = (o1: ICardTemplate | null, o2: ICardTemplate | null): boolean =>
    this.cardTemplateService.compareCardTemplate(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cardMetric }) => {
      this.cardMetric = cardMetric;
      if (cardMetric) {
        this.updateForm(cardMetric);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cardMetric = this.cardMetricFormService.getCardMetric(this.editForm);
    if (cardMetric.id !== null) {
      this.subscribeToSaveResponse(this.cardMetricService.update(cardMetric));
    } else {
      this.subscribeToSaveResponse(this.cardMetricService.create(cardMetric));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICardMetric>>): void {
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

  protected updateForm(cardMetric: ICardMetric): void {
    this.cardMetric = cardMetric;
    this.cardMetricFormService.resetForm(this.editForm, cardMetric);

    this.cardTemplatesSharedCollection = this.cardTemplateService.addCardTemplateToCollectionIfMissing<ICardTemplate>(
      this.cardTemplatesSharedCollection,
      cardMetric.cardTemplate
    );
  }

  protected loadRelationshipsOptions(): void {
    this.cardTemplateService
      .query()
      .pipe(map((res: HttpResponse<ICardTemplate[]>) => res.body ?? []))
      .pipe(
        map((cardTemplates: ICardTemplate[]) =>
          this.cardTemplateService.addCardTemplateToCollectionIfMissing<ICardTemplate>(cardTemplates, this.cardMetric?.cardTemplate)
        )
      )
      .subscribe((cardTemplates: ICardTemplate[]) => (this.cardTemplatesSharedCollection = cardTemplates));
  }
}
