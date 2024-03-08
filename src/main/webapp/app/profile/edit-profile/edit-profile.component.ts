import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'jhi-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss'],
})
export class EditProfileComponent implements OnInit {
  background: string = 'black';
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

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit(): void {}
  onSubmit(): void {
    this.username = this.preferencesForm.get('username')?.value;
    this.pic = this.preferencesForm.get('pic')?.value;
    this.bio = this.preferencesForm.get('bio')?.value;
    this.profilePriv = this.preferencesForm.get('profilePriv')?.value;
    this.playlistPriv = this.preferencesForm.get('playlistPriv')?.value;
    this.darkMode = this.preferencesForm.get('darkMode')?.value;
    if (this.darkMode == false) {
      this.background = 'white';
    }
    this.highContrast = this.preferencesForm.get('highContrast')?.value;
  }
}
