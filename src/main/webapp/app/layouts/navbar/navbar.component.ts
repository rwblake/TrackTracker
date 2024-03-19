import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { APP_NAME, VERSION } from 'app/app.constants';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { EntityNavbarItems } from 'app/entities/entity-navbar-items';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit {
  inProduction?: boolean;
  isNavbarCollapsed = true;
  openAPIEnabled?: boolean;
  version = '';
  account: Account | null = null;
  entitiesNavbarItems: any[] = [];

  constructor(
    private loginService: LoginService,
    private accountService: AccountService,
    private profileService: ProfileService,
    private router: Router
  ) {
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`;
    }
  }

  ngOnInit(): void {
    this.entitiesNavbarItems = EntityNavbarItems;
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
      this.openAPIEnabled = profileInfo.openAPIEnabled;
    });

    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
  }

  toProfile() {
    this.collapseNavbar();
    this.router.navigate(['/profile', this.account?.firstName]);
  }

  showDropdown() {
    let dropdown = document.getElementById('dropdown');
    // @ts-ignore
    dropdown.style.right = '0';
    // @ts-ignore
    if (window.getComputedStyle(dropdown).display === 'block') {
      // @ts-ignore
      dropdown.style.display = 'none';
    } else {
      // @ts-ignore
      dropdown.style.display = 'block';
    }
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
    const dropdown = document.getElementById('dropdown');
    if (dropdown !== null) dropdown.style.display = 'none';
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }

  edit(): void {
    this.collapseNavbar();
    this.router.navigate(['./profile/edit-profile']);
  }

  toFriends(): void {
    this.collapseNavbar();
    this.router.navigate(['./friends']);
  }
  showModal() {
    this.collapseNavbar();
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
  navigateToGDPR() {
    this.collapseNavbar();
    this.router.navigate(['/gdpr-policy']);
  }

  toSettings() {
    this.collapseNavbar();
    this.router.navigate(['/account/settings']);
  }

  toChangePassword() {
    this.collapseNavbar();
    this.router.navigate(['/account/password']);
  }

  toggleNavbar(): void {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  protected readonly APP_NAME = APP_NAME;
}
