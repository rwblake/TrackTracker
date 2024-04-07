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

  ngOnInit() {
    this.entitiesNavbarItems = EntityNavbarItems;
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
      this.openAPIEnabled = profileInfo.openAPIEnabled;
    });

    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
    });
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

  collapseNavbar() {
    this.isNavbarCollapsed = true;
    const dropdown = document.getElementById('dropdown');
    if (dropdown !== null) dropdown.style.display = 'none';
  }

  async logout() {
    this.collapseNavbar();
    this.loginService.logout();
    await this.router.navigate(['']);
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

  toggleNavbar() {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  protected readonly APP_NAME = APP_NAME;
}
