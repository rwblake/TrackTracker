import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStream, NewStream } from '../stream.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStream for edit and NewStreamFormGroupInput for create.
 */
type StreamFormGroupInput = IStream | PartialWithRequiredKeyOf<NewStream>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IStream | NewStream> = Omit<T, 'playedAt'> & {
  playedAt?: string | null;
};

type StreamFormRawValue = FormValueOf<IStream>;

type NewStreamFormRawValue = FormValueOf<NewStream>;

type StreamFormDefaults = Pick<NewStream, 'id' | 'playedAt'>;

type StreamFormGroupContent = {
  id: FormControl<StreamFormRawValue['id'] | NewStream['id']>;
  playedAt: FormControl<StreamFormRawValue['playedAt']>;
  song: FormControl<StreamFormRawValue['song']>;
  appUser: FormControl<StreamFormRawValue['appUser']>;
};

export type StreamFormGroup = FormGroup<StreamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StreamFormService {
  createStreamFormGroup(stream: StreamFormGroupInput = { id: null }): StreamFormGroup {
    const streamRawValue = this.convertStreamToStreamRawValue({
      ...this.getFormDefaults(),
      ...stream,
    });
    return new FormGroup<StreamFormGroupContent>({
      id: new FormControl(
        { value: streamRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      playedAt: new FormControl(streamRawValue.playedAt, {
        validators: [Validators.required],
      }),
      song: new FormControl(streamRawValue.song, {
        validators: [Validators.required],
      }),
      appUser: new FormControl(streamRawValue.appUser),
    });
  }

  getStream(form: StreamFormGroup): IStream | NewStream {
    return this.convertStreamRawValueToStream(form.getRawValue() as StreamFormRawValue | NewStreamFormRawValue);
  }

  resetForm(form: StreamFormGroup, stream: StreamFormGroupInput): void {
    const streamRawValue = this.convertStreamToStreamRawValue({ ...this.getFormDefaults(), ...stream });
    form.reset(
      {
        ...streamRawValue,
        id: { value: streamRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StreamFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      playedAt: currentTime,
    };
  }

  private convertStreamRawValueToStream(rawStream: StreamFormRawValue | NewStreamFormRawValue): IStream | NewStream {
    return {
      ...rawStream,
      playedAt: dayjs(rawStream.playedAt, DATE_TIME_FORMAT),
    };
  }

  private convertStreamToStreamRawValue(
    stream: IStream | (Partial<NewStream> & StreamFormDefaults)
  ): StreamFormRawValue | PartialWithRequiredKeyOf<NewStreamFormRawValue> {
    return {
      ...stream,
      playedAt: stream.playedAt ? stream.playedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
