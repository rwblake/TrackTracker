import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { IFriendship } from 'app/entities/friendship/friendship.model';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FriendsService } from './friends.service';
import { PlaylistInsightsResponse } from '../playlist-insights/playlist-insights-response-interface';

@Component({
  selector: 'jhi-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.scss'],
})
export class FriendsComponent implements OnInit {
  friendRequestForm: FormGroup = new FormGroup({ name: new FormControl('', [Validators.required, Validators.min(0)]) });

  pulledData: boolean = false;
  waitingForResponse: boolean = false;
  showWaitingMessage: boolean = false;
  showErrorMessage: boolean = false;

  linkInput: string = '';

  response: IFriendship | undefined;

  constructor(private titleService: Title, private friendsService: FriendsService) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - Friends');
  }

  async sendLink() {
    // @ts-ignore
    const url: string = this.friendRequestForm.get('name').value;

    this.waitingForResponse = true;
    this.pulledData = false;
    this.showErrorMessage = false;

    this.friendsService.sendURL(parseInt(url)).subscribe({
      next: v => this.onSuccessfulResponse(v),
      error: e => this.onFailure(e),
    });

    await this.delay(750);
    if (this.waitingForResponse) {
      this.showWaitingMessage = true;
    }
  }

  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  onFailure(error: any) {
    this.waitingForResponse = false;
    this.showWaitingMessage = false;
    this.pulledData = false;
    this.showErrorMessage = true;
  }

  onSuccessfulResponse(val: IFriendship) {
    this.waitingForResponse = false;
    this.showWaitingMessage = false;

    this.response = val;

    // Reveal the lower section of the page.
    this.pulledData = true;
  }
}
