import { Component, OnInit } from '@angular/core';
import { Router, NavigationStart, Event as NavigationEvent } from '@angular/router';
import { HTTPStatus } from './auth/token-interceptor';
import { AuthenticationService } from './shared/services/authentication.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  currentUrl;
  userName: any;
  isUserDropdownOpen: boolean = true;
  isLocationDropdownOpen: boolean = false;
  userCharLogo: string;
  HTTPActivity: boolean;

  constructor(
    private readonly router: Router,
    private readonly authenticationService: AuthenticationService,
    private readonly httpStatus: HTTPStatus
    ) { }

  ngOnInit() {
    this.prerequisite();
  }

  prerequisite() {
    this.getCurrentPage();
    this.checkAPIStatus();
  }

  checkAPIStatus() {
    this.httpStatus.getHttpStatus().subscribe((status: boolean) => {
      this.HTTPActivity = status;
    });
  }

  getCurrentPage() {
    this.router.events.subscribe((event: NavigationEvent) => {
      if (event instanceof NavigationStart) {
        this.currentUrl = event.url;
        if (this.currentUrl !== '/login' && this.currentUrl !== '/signup') {
          this.getLoggedInUser();
        }
      }
    });
  }

  getLoggedInUser() {
    this.userName = localStorage.getItem('Username');
    if (!this.userName) {
      this.authenticationService.getLoggedInUser().subscribe(res => {
        if (res) {
          const userNameArr = res.userName.split(' ');
          this.userName = res.userName;
          this.userCharLogo = `${userNameArr[0].toString().charAt(0).toUpperCase()}${userNameArr[1].toString().charAt(0).toUpperCase()}`;
          localStorage.setItem('Username', this.userName);
        }
      });
    }
  }

  hideCurrentDropdown(id) {
    switch (id) {
      case 1:
        this.isUserDropdownOpen = !this.isUserDropdownOpen;
        break;
      case 2:
        this.isLocationDropdownOpen = !this.isLocationDropdownOpen;
        break;
    }
  }
}
