import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { IFriendship } from 'app/entities/friendship/friendship.model';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FriendsService } from './friends.service';
import { IFriendRequest } from '../entities/friend-request/friend-request.model';
import { IFriend } from './friend.model';
import { IAppUser } from '../entities/app-user/app-user.model';
import relativeTime from 'dayjs/esm/plugin/relativeTime';
import dayjs from 'dayjs/esm';
import { IUser } from '../entities/user/user.model';
import { IFriendRecommendation } from '../entities/friend-recommendation/friend-recommendation.model';
import { ISong } from '../entities/song/song.model';
dayjs.extend(relativeTime);

@Component({
  selector: 'jhi-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.scss'],
})
export class FriendsComponent implements OnInit {
  friendRequestForm: FormGroup = new FormGroup({ name: new FormControl('', [Validators.required, Validators.min(0)]) });

  pulledData: boolean = false;
  showErrorMessage: boolean = false;

  search: string = '';

  createFriendRequestResponse: IFriendship | undefined;
  friendRequests?: IFriendRequest[];
  friends?: IFriend[];
  users?: IAppUser[];
  dialog: boolean;
  recommendations?: IFriendRecommendation[];
  blocked?: IAppUser[];

  constructor(private titleService: Title, private friendsService: FriendsService) {
    this.dialog = false;
  }

  ngOnInit() {
    this.titleService.setTitle(APP_NAME + ' - Friends');
    this.reload();
  }

  reload() {
    this.loadRecommendations();
    this.loadFriendRequests();
    this.loadFriends();
    this.loadUsers();
    this.loadBlocked();
  }

  since(time: dayjs.Dayjs | null | undefined): string {
    return dayjs().from(time, true);
  }

  toggledialog() {
    this.dialog = !this.dialog;
  }

  getDisplayName(user?: Pick<IUser, 'id' | 'login' | 'firstName' | 'lastName'> | null): string {
    if (user?.lastName != null && user?.firstName != null) {
      return user.firstName + ' ' + user.lastName;
    } else {
      return user?.login ?? '';
    }
  }

  getArtistNames(song: ISong): string | undefined {
    return song.artists
      ?.filter(artist => !!artist.name)
      ?.map(artist => artist.name!)
      ?.join(', ');
  }

  toPercent(n: number | null | undefined): number {
    // @ts-ignore
    return n * 100;
  }

  loadFriendRequests(): void {
    this.friendsService.getFriendRequests().subscribe({
      next: v => (this.friendRequests = v),
    });
  }

  loadFriends(): void {
    this.friendsService.getFriends().subscribe({
      next: v => {
        this.friends = v;
        console.log(this.friends);
      },
    });
  }

  loadBlocked(): void {
    this.friendsService.getBlocked().subscribe({
      next: v => (this.blocked = v),
    });
  }

  loadUsers(): void {
    this.friendsService.getUsers().subscribe({
      next: v => (this.users = v),
    });
  }

  loadRecommendations(): void {
    this.friendsService.getRecommendations().subscribe({
      next: v => (this.recommendations = v),
    });
  }

  sendFriendRequest(id?: number) {
    if (id == null) {
      return;
    }
    this.showErrorMessage = false;
    this.friendsService.sendFriendRequest(id).subscribe({
      next: v => this.reload(),
    });
  }

  // Accept a friend request by id
  accept(id: number) {
    this.friendsService.acceptFriendRequest(id).subscribe({
      next: v => this.reload(),
    });
  }

  // Block a user by their id
  block(id: number) {
    this.friendsService.block(id).subscribe({
      next: v => this.reload(),
    });
  }

  // Unblock a user by their id
  unblock(id: number) {
    this.friendsService.unblock(id).subscribe({
      next: v => this.reload(),
    });
  }

  // Reject a friend request by id
  reject(id: number) {
    this.friendsService.rejectFriendRequest(id).subscribe({
      next: v => this.reload(),
    });
  }

  // Delete a friend (unfriend them)
  delete(friend: IFriend) {
    this.friendsService.delete(friend).subscribe(() => this.reload());
  }

  protected readonly Number = Number;
}
