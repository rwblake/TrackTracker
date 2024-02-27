import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { SpotifyTokenFormService, SpotifyTokenFormGroup } from './spotify-token-form.service';
import { ISpotifyToken } from '../spotify-token.model';
import { SpotifyTokenService } from '../service/spotify-token.service';

@Component({
  selector: 'jhi-spotify-token-update',
  templateUrl: './spotify-token-update.component.html',
})
export class SpotifyTokenUpdateComponent implements OnInit {
  isSaving = false;
  spotifyToken: ISpotifyToken | null = null;

  editForm: SpotifyTokenFormGroup = this.spotifyTokenFormService.createSpotifyTokenFormGroup();

  constructor(
    protected spotifyTokenService: SpotifyTokenService,
    protected spotifyTokenFormService: SpotifyTokenFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spotifyToken }) => {
      this.spotifyToken = spotifyToken;
      if (spotifyToken) {
        this.updateForm(spotifyToken);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const spotifyToken = this.spotifyTokenFormService.getSpotifyToken(this.editForm);
    if (spotifyToken.id !== null) {
      this.subscribeToSaveResponse(this.spotifyTokenService.update(spotifyToken));
    } else {
      this.subscribeToSaveResponse(this.spotifyTokenService.create(spotifyToken));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpotifyToken>>): void {
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

  protected updateForm(spotifyToken: ISpotifyToken): void {
    this.spotifyToken = spotifyToken;
    this.spotifyTokenFormService.resetForm(this.editForm, spotifyToken);
  }
}
