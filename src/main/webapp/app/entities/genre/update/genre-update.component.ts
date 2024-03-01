import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { GenreFormService, GenreFormGroup } from './genre-form.service';
import { IGenre } from '../genre.model';
import { GenreService } from '../service/genre.service';

@Component({
  selector: 'jhi-genre-update',
  templateUrl: './genre-update.component.html',
})
export class GenreUpdateComponent implements OnInit {
  isSaving = false;
  genre: IGenre | null = null;

  editForm: GenreFormGroup = this.genreFormService.createGenreFormGroup();

  constructor(
    protected genreService: GenreService,
    protected genreFormService: GenreFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ genre }) => {
      this.genre = genre;
      if (genre) {
        this.updateForm(genre);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const genre = this.genreFormService.getGenre(this.editForm);
    if (genre.id !== null) {
      this.subscribeToSaveResponse(this.genreService.update(genre));
    } else {
      this.subscribeToSaveResponse(this.genreService.create(genre));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGenre>>): void {
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

  protected updateForm(genre: IGenre): void {
    this.genre = genre;
    this.genreFormService.resetForm(this.editForm, genre);
  }
}
