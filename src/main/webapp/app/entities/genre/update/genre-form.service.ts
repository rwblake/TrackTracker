import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGenre, NewGenre } from '../genre.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGenre for edit and NewGenreFormGroupInput for create.
 */
type GenreFormGroupInput = IGenre | PartialWithRequiredKeyOf<NewGenre>;

type GenreFormDefaults = Pick<NewGenre, 'id' | 'artists'>;

type GenreFormGroupContent = {
  id: FormControl<IGenre['id'] | NewGenre['id']>;
  name: FormControl<IGenre['name']>;
  artists: FormControl<IGenre['artists']>;
};

export type GenreFormGroup = FormGroup<GenreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GenreFormService {
  createGenreFormGroup(genre: GenreFormGroupInput = { id: null }): GenreFormGroup {
    const genreRawValue = {
      ...this.getFormDefaults(),
      ...genre,
    };
    return new FormGroup<GenreFormGroupContent>({
      id: new FormControl(
        { value: genreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(genreRawValue.name),
      artists: new FormControl(genreRawValue.artists ?? []),
    });
  }

  getGenre(form: GenreFormGroup): IGenre | NewGenre {
    return form.getRawValue() as IGenre | NewGenre;
  }

  resetForm(form: GenreFormGroup, genre: GenreFormGroupInput): void {
    const genreRawValue = { ...this.getFormDefaults(), ...genre };
    form.reset(
      {
        ...genreRawValue,
        id: { value: genreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GenreFormDefaults {
    return {
      id: null,
      artists: [],
    };
  }
}
