import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
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
    private readonly httpStatus: HTTPStatus,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.prerequisite();
  }

  ngAfterViewChecked() {
    this.cdr.detectChanges();
  }

  prerequisite() {
    this.getCurrentPage();
    this.checkTOkenExpiresOrNot();
    this.checkAPIStatus();
  }

  checkTOkenExpiresOrNot() {
    const tokenExpiryDate = JSON.parse(localStorage.getItem('refresh_token_expiry_time'));
    const tokenExpiry = tokenExpiryDate
      ? new Date(tokenExpiryDate)
      : null;
    // Check if token is expired or not
    if (tokenExpiry && tokenExpiry <= new Date()) {
      // token has expired user should be logged out
      this.router.navigate(['/login']);
      localStorage.clear();
    } else { }
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

  getUserCharLogo(username) {
    const userNameArr = username.split(' ');
    return `${userNameArr[0].toString().charAt(0).toUpperCase()}${userNameArr[1].toString().charAt(0).toUpperCase()}`;
  }

  getLoggedInUser() {
    this.userName = localStorage.getItem('Username');
    this.userCharLogo = this.getUserCharLogo(this.userName);
    if (!this.userName) {
      this.authenticationService.getLoggedInUser().subscribe(res => {
        if (res) {
          const userNameArr = res.userName.split(' ');
          this.userName = res.userName;
          this.userCharLogo = this.getUserCharLogo(this.userName);
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
