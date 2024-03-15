import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { IFriendship } from 'app/entities/friendship/friendship.model';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FriendsService } from './friends.service';
import { IFriendRequest } from '../entities/friend-request/friend-request.model';
import { IFriend } from './friend.model';

@Component({
  selector: 'jhi-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.scss'],
})
export class FriendsComponent implements OnInit {
  friendRequestForm: FormGroup = new FormGroup({ name: new FormControl('', [Validators.required, Validators.min(0)]) });

  pulledData: boolean = false;
  showErrorMessage: boolean = false;

  linkInput: string = '';

  createFriendRequestResponse: IFriendship | undefined;
  friendRequests?: IFriendRequest[];
  friends?: IFriend[];

  constructor(private titleService: Title, private friendsService: FriendsService) {}

  ngOnInit() {
    this.titleService.setTitle(APP_NAME + ' - Friends');
    this.loadFriendRequests();
    this.loadFriends();
  }

  loadFriendRequests(): void {
    this.friendsService.getFriendRequests().subscribe({
      next: v => (this.friendRequests = v),
    });
  }

  loadFriends(): void {
    this.friendsService.getFriends().subscribe({
      next: v => (this.friends = v),
    });
  }

  async sendLink() {
    // @ts-ignore
    const url: string = this.friendRequestForm.get('name').value;
    this.showErrorMessage = false;
    this.friendsService.sendURL(parseInt(url)).subscribe({
      next: v => this.onSuccessfulFriendRequestCreationResponse(v),
      error: e => (this.showErrorMessage = true),
    });
  }

  onSuccessfulFriendRequestCreationResponse(val: IFriendship) {
    this.createFriendRequestResponse = val;
    // Reveal the lower section of the page.
    this.pulledData = true;
    this.loadFriends();
  }

  // Accept a friend request by id
  accept(id: number) {
    this.friendsService.acceptFriendRequest(id).subscribe({
      next: v => (this.friendRequests = v),
    });
  }

  // Reject a friend request by id
  reject(id: number) {
    this.friendsService.rejectFriendRequest(id).subscribe({
      next: v => (this.friendRequests = v),
    });
  }
}
