import { Component, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE, SPOTIFY_ID_ALREADY_USED_TYPE } from 'app/config/error.constants';
import { RegisterService } from './register.service';

@Component({
  selector: 'jhi-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements AfterViewInit {
  @ViewChild('login', { static: false })
  login?: ElementRef;

  doNotMatch = false;
  error = false;
  errorEmailExists = false;
  errorUserExists = false;
  errorSpotifyUserExists = false;
  success = false;

  registerForm = new FormGroup({
    login: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    bio: new FormControl('', {
      nonNullable: false,
      validators: [Validators.maxLength(200)],
    }),

    // Below section won't be there in final UI
    spotifyID: new FormControl('', {
      nonNullable: true,
      validators: [Validators.minLength(22), Validators.maxLength(22)],
    }),
    accessToken: new FormControl('', {
      nonNullable: true,
    }),
    tokenType: new FormControl('', {
      nonNullable: true,
    }),
    scope: new FormControl('', {
      nonNullable: true,
    }),
    expiresIn: new FormControl('', {
      nonNullable: true,
      // validators: [Validators.pattern('[a-zA-Z ]*')],
    }),
    refreshToken: new FormControl('', {
      nonNullable: true,
    }),
    // --------

    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
  });

  constructor(private registerService: RegisterService) {}

  ngAfterViewInit(): void {
    if (this.login) {
      this.login.nativeElement.focus();
    }
  }

  register(): void {
    this.doNotMatch = false;
    this.error = false;
    this.errorEmailExists = false;
    this.errorUserExists = false;
    this.errorSpotifyUserExists = false;

    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch = true;
    } else {
      const { login, email, bio, spotifyID, accessToken, tokenType, scope, expiresIn, refreshToken } = this.registerForm.getRawValue();
      const num_expiresIn = Number.parseInt(expiresIn);
      this.registerService
        .save({
          login,
          email,
          password,
          bio,
          spotifyID,
          credentials: {
            accessToken,
            tokenType,
            scope,
            expiresIn: num_expiresIn,
            refreshToken,
          },
          langKey: 'en',
        })
        .subscribe({ next: () => (this.success = true), error: response => this.processError(response) });
    }
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.errorUserExists = true;
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.errorEmailExists = true;
    } else if (response.status === 400 && response.error.type === SPOTIFY_ID_ALREADY_USED_TYPE) {
      this.errorSpotifyUserExists = true;
    } else {
      this.error = true;
    }
  }
}
