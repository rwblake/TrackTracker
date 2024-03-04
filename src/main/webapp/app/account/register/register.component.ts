import { Component, AfterViewInit, ElementRef, ViewChild, HostListener } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
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
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4), Validators.maxLength(50)],
    }),
    spotifyAuthCode: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    spotifyAuthState: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  constructor(private registerService: RegisterService) {}

  ngAfterViewInit(): void {
    if (this.login) {
      this.login.nativeElement.focus();
    }
  }

  @HostListener('window:message', ['$event'])
  onMessage(event: MessageEvent): void {
    if (typeof event.data === 'string') {
      const pattern = /^code=(.*)&state=(.*)$/;
      if (pattern.test(event.data)) {
        const results = pattern.exec(event.data);
        if (results !== null) {
          const code = results[1],
            state = results[2];
          this.registerForm.patchValue({ spotifyAuthCode: code, spotifyAuthState: state });
        }
      }
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
      const { login, email, bio, spotifyAuthState, spotifyAuthCode } = this.registerForm.getRawValue();
      this.registerService
        .save({
          login,
          email,
          password,
          bio,
          spotifyAuthCode,
          spotifyAuthState,
          langKey: 'en',
        })
        .subscribe({ next: () => (this.success = true), error: response => this.processAuthenticationURIError(response) });
    }
  }

  openSpotifyAuthPopup(): void {
    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch = true;
      return;
    }

    this.registerService.getAuthenticationURI().subscribe({
      next: this.processAuthenticationURIResponse,
      error: this.processAuthenticationURIError,
    });
  }

  private processAuthenticationURIResponse(response: HttpResponse<URL>): void {
    if (response.body == null) {
      this.error = true;
      console.error('An error occurred attempting to fetch the spotify authentication URI: response.body=null');
      return;
    }

    // Open Spotify authentication URI as popup
    const authorisationURL = response.body.toString();
    const popupFeatures = Object.entries({
      popup: true,
      width: 500,
      height: window.innerHeight,
      left: window.innerWidth / 2 - 250,
    })
      .map(([key, val]) => `${key}=${val.toString()}`)
      .join(',');

    const newWindow = window.open(authorisationURL, '', popupFeatures);
    if (newWindow == null) {
      console.error('An error occured attempting to open the spotify authentication popup');
      this.error = true;
    }
  }

  private processAuthenticationURIError(response: HttpErrorResponse): void {
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
