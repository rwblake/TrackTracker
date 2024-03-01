import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFeedCard, NewFeedCard } from '../feed-card.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFeedCard for edit and NewFeedCardFormGroupInput for create.
 */
type FeedCardFormGroupInput = IFeedCard | PartialWithRequiredKeyOf<NewFeedCard>;

type FeedCardFormDefaults = Pick<NewFeedCard, 'id' | 'liked'>;

type FeedCardFormGroupContent = {
  id: FormControl<IFeedCard['id'] | NewFeedCard['id']>;
  liked: FormControl<IFeedCard['liked']>;
  feed: FormControl<IFeedCard['feed']>;
  card: FormControl<IFeedCard['card']>;
};

export type FeedCardFormGroup = FormGroup<FeedCardFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FeedCardFormService {
  createFeedCardFormGroup(feedCard: FeedCardFormGroupInput = { id: null }): FeedCardFormGroup {
    const feedCardRawValue = {
      ...this.getFormDefaults(),
      ...feedCard,
    };
    return new FormGroup<FeedCardFormGroupContent>({
      id: new FormControl(
        { value: feedCardRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      liked: new FormControl(feedCardRawValue.liked, {
        validators: [Validators.required],
      }),
      feed: new FormControl(feedCardRawValue.feed, {
        validators: [Validators.required],
      }),
      card: new FormControl(feedCardRawValue.card, {
        validators: [Validators.required],
      }),
    });
  }

  getFeedCard(form: FeedCardFormGroup): IFeedCard | NewFeedCard {
    return form.getRawValue() as IFeedCard | NewFeedCard;
  }

  resetForm(form: FeedCardFormGroup, feedCard: FeedCardFormGroupInput): void {
    const feedCardRawValue = { ...this.getFormDefaults(), ...feedCard };
    form.reset(
      {
        ...feedCardRawValue,
        id: { value: feedCardRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FeedCardFormDefaults {
    return {
      id: null,
      liked: false,
    };
  }
}
