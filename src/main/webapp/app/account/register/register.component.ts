import { Component, AfterViewInit, ElementRef, ViewChild, OnInit, TemplateRef } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { FormGroup, FormControl, Validators, AbstractControl } from '@angular/forms';

import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE, SPOTIFY_ID_ALREADY_USED_TYPE } from 'app/config/error.constants';
import { RegisterService } from './register.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';

function nameValidator(control: AbstractControl): { [key: string]: any } | null {
  const valid = /^[a-zA-Z]*$/.test(control.value);
  return valid ? null : { invalidName: true };
}

@Component({
  selector: 'jhi-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit, AfterViewInit {
  @ViewChild('focusStart', { static: false })
  focusStart?: ElementRef;

  doNotMatch = false;
  error = false;
  errorEmailExists = false;
  errorUserExists = false;
  errorSpotifyUserExists = false;
  success = false;

  registerForm = new FormGroup({
    firstName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(120), nameValidator],
    }),
    lastName: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(1), Validators.maxLength(120), nameValidator],
    }),
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
    // bio: new FormControl('', {
    //   nonNullable: false,
    //   validators: [Validators.maxLength(200)],
    // }),
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
    agreeGdpr: new FormControl(false, {
      validators: [Validators.requiredTrue],
    }),
  });

  constructor(private registerService: RegisterService, private modalService: NgbModal, private router: Router) {}

  public ngOnInit(): void {
    // Access previous form data
    const formData = localStorage.getItem('registrationFormData');
    if (formData) {
      const parsedFormData = JSON.parse(formData);
      // Populate the registration form with the persisted data
      this.registerForm.patchValue(parsedFormData);
      // Clear the stored form data after populating the form
      localStorage.removeItem('registrationFormData');
    }

    // Check if callback URL contains authorization code and state
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');

    // Handle authorization code and state
    if (code && state) this.registerForm.patchValue({ spotifyAuthCode: code, spotifyAuthState: state });
  }

  public ngAfterViewInit(): void {
    if (this.focusStart)
      // Initially focus on the first field of the form
      this.focusStart.nativeElement.focus();
  }

  // Attempts to register user - final stage
  public register(): void {
    this.doNotMatch = false;
    this.error = false;
    this.errorEmailExists = false;
    this.errorUserExists = false;
    this.errorSpotifyUserExists = false;

    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch = true;
    } else {
      const { firstName, lastName, login, email, spotifyAuthState, spotifyAuthCode } = this.registerForm.getRawValue();
      this.registerService
        .save({
          firstName,
          lastName,
          login,
          email,
          password,
          spotifyAuthCode,
          spotifyAuthState,
          langKey: 'en',
        })
        .subscribe({ next: () => (this.success = true), error: response => this.processAuthenticationURIError(response) });
    }
  }

  // Opens the Spotify Auth Popup
  public openSpotifyAuthPopup(): void {
    const { password, confirmPassword } = this.registerForm.getRawValue();
    if (password !== confirmPassword) {
      this.doNotMatch = true;
      return;
    }

    const formData = this.registerForm.getRawValue();
    localStorage.setItem('registrationFormData', JSON.stringify(formData));

    this.registerService.getAuthenticationURI().subscribe({
      next: this.processAuthenticationURIResponse,
      error: this.processAuthenticationURIError,
    });
  }

  // Unlinks a user's account from the login, allowing them to re-link with another
  public unlinkAccount(): void {
    // Strip the URL of the query params, and remove the auth codes
    this.router.navigateByUrl(this.router.url.split('?')[0]);
    this.registerForm.get('spotifyAuthCode')?.setValue('');
    this.registerForm.get('spotifyAuthState')?.setValue('');
  }

  private processAuthenticationURIResponse(response: HttpResponse<URL>): void {
    if (response.body == null) {
      this.error = true;
      console.error('An error occurred attempting to fetch the spotify authentication URI: response.body=null');
      return;
    }
    window.location.href = response.body.toString();
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

  openModal(content: TemplateRef<any>) {
    this.modalService.open(content, { scrollable: true });
  }
}
