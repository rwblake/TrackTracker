import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { APP_NAME } from '../app.constants';
import { Router } from '@angular/router';
import { LoginService } from 'app/login/login.service';
import { AppUserService } from '../entities/app-user/service/app-user.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'jhi-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  appUser: any;
  modal = document.getElementById('myModal');
  btn = document.getElementById('helpBtn');
  span = document.getElementsByClassName('close')[0];

  constructor(
    private titleService: Title,
    private router: Router,
    private loginService: LoginService,
    private appUserService: AppUserService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle(APP_NAME + ' - My Profile');
    // this.appUserService.find(55911).subscribe(
    //   (res) => {
    //     this.appUser = res.body;
    //   },
    //   (error) => {
    //     console.error('Error fetching user data:', error);
    //   }
    // );
  }
  edit(): void {
    this.router.navigate(['./profile/edit-profile']);
  }
  settings(): void {
    this.router.navigate(['./profile/user-preferences']);
  }
  logout(): void {
    this.loginService.logout();
    this.router.navigate(['']);
  }
  showModal() {
    let modal = document.getElementById('myModal') as HTMLElement | null;
    if (modal) {
      modal.style.display = 'block';
    } else {
      console.error('Modal element not found');
    }
  }
  closeModal() {
    let modal = document.getElementById('myModal') as HTMLElement | null;
    if (modal) {
      modal.style.display = 'none';
    } else {
      console.error('Modal element not found');
    }
  }
}
