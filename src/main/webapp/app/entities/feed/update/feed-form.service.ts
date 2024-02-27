import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFeed, NewFeed } from '../feed.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFeed for edit and NewFeedFormGroupInput for create.
 */
type FeedFormGroupInput = IFeed | PartialWithRequiredKeyOf<NewFeed>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFeed | NewFeed> = Omit<T, 'lastUpdated'> & {
  lastUpdated?: string | null;
};

type FeedFormRawValue = FormValueOf<IFeed>;

type NewFeedFormRawValue = FormValueOf<NewFeed>;

type FeedFormDefaults = Pick<NewFeed, 'id' | 'lastUpdated'>;

type FeedFormGroupContent = {
  id: FormControl<FeedFormRawValue['id'] | NewFeed['id']>;
  lastUpdated: FormControl<FeedFormRawValue['lastUpdated']>;
};

export type FeedFormGroup = FormGroup<FeedFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FeedFormService {
  createFeedFormGroup(feed: FeedFormGroupInput = { id: null }): FeedFormGroup {
    const feedRawValue = this.convertFeedToFeedRawValue({
      ...this.getFormDefaults(),
      ...feed,
    });
    return new FormGroup<FeedFormGroupContent>({
      id: new FormControl(
        { value: feedRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      lastUpdated: new FormControl(feedRawValue.lastUpdated, {
        validators: [Validators.required],
      }),
    });
  }

  getFeed(form: FeedFormGroup): IFeed | NewFeed {
    return this.convertFeedRawValueToFeed(form.getRawValue() as FeedFormRawValue | NewFeedFormRawValue);
  }

  resetForm(form: FeedFormGroup, feed: FeedFormGroupInput): void {
    const feedRawValue = this.convertFeedToFeedRawValue({ ...this.getFormDefaults(), ...feed });
    form.reset(
      {
        ...feedRawValue,
        id: { value: feedRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FeedFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastUpdated: currentTime,
    };
  }

  private convertFeedRawValueToFeed(rawFeed: FeedFormRawValue | NewFeedFormRawValue): IFeed | NewFeed {
    return {
      ...rawFeed,
      lastUpdated: dayjs(rawFeed.lastUpdated, DATE_TIME_FORMAT),
    };
  }

  private convertFeedToFeedRawValue(
    feed: IFeed | (Partial<NewFeed> & FeedFormDefaults)
  ): FeedFormRawValue | PartialWithRequiredKeyOf<NewFeedFormRawValue> {
    return {
      ...feed,
      lastUpdated: feed.lastUpdated ? feed.lastUpdated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
