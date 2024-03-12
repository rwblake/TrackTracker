import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { takeUntil } from 'rxjs/operators';
import { AccountService } from '../../core/auth/account.service';
import { Account } from '../../core/auth/account.model';
import { Subject } from 'rxjs';

@Component({
  selector: 'jhi-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss'],
})
export class EditProfileComponent implements OnInit {
  private readonly destroy$ = new Subject<void>();
  account: Account | null = null;
  //background: string = 'black';
  username: string | undefined;
  pic: string | undefined;
  bio: string | undefined;
  profilePriv: number | undefined;
  playlistPriv: number | undefined;
  darkMode: boolean | undefined;
  highContrast: boolean | undefined;

  preferencesForm: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    pic: new FormControl('', [Validators.required]),
    bio: new FormControl('', [Validators.required]),
    profilePriv: new FormControl('', [Validators.required]),
    playlistPriv: new FormControl('', [Validators.required]),
    darkMode: new FormControl('', [Validators.required]),
    highContrast: new FormControl('', [Validators.required]),
  });

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
  }
  onSubmit(): void {
    this.username = this.preferencesForm.get('username')?.value;
    if (this.username == '') {
      this.username = this.account?.login;
    }
    this.pic = this.preferencesForm.get('pic')?.value;
    if (this.pic == '') {
      this.pic = 'https://cse.iutoic-dhaka.edu/uploads/img/1601007481_1328.jpg';
    }
    this.bio = this.preferencesForm.get('bio')?.value;
    if (this.bio == '') {
      this.bio = '';
    }
    this.profilePriv = this.preferencesForm.get('profilePriv')?.value;
    if (this.profilePriv == 0) {
      this.profilePriv = 0;
    }
    this.playlistPriv = this.preferencesForm.get('playlistPriv')?.value;
    if (this.playlistPriv == 0) {
      this.playlistPriv = 0;
    }
    this.darkMode = this.preferencesForm.get('darkMode')?.value;
    // if (this.darkMode == false) {
    //   this.background = 'white';
    // }
    if (this.darkMode == false) {
      this.darkMode = false;
    }
    this.highContrast = this.preferencesForm.get('highContrast')?.value;
    if (this.highContrast == false) {
      this.highContrast = false;
    }
  }
}
