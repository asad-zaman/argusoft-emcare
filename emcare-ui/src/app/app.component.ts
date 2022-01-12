import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, NavigationStart, Event as NavigationEvent } from '@angular/router';
import { HTTPStatus } from './auth/token-interceptor';
import { AuthenticationService } from './shared/services/authentication.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  currentUrl:string = '';
  userName: any;
  isUserDropdownOpen: boolean = true;
  isLocationDropdownOpen: boolean = false;
  userCharLogo: string;
  HTTPActivity: boolean;
  user: any;
  featureList: any = [];
  isLoggedIn: boolean = false;

  constructor(
    private readonly router: Router,
    private readonly authenticationService: AuthenticationService,
    private readonly httpStatus: HTTPStatus,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.prerequisite();
    console.log('asd');
  }

  ngAfterViewChecked() {
    this.cdr.detectChanges();
  }

  prerequisite() {
    this.getCurrentPage();
    this.checkTOkenExpiresOrNot();
    this.checkAPIStatus();
    this.authenticationService.getIsLoggedIn().subscribe(result => {
      if (result) {
        this.getFeatureList();
      }
    })
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
    } else if (!!tokenExpiryDate) { 
      this.authenticationService.setIsLoggedIn(true);
    } else {
    }
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

  getFeatureList() {
    this.authenticationService.getLoggedInUser().subscribe(res => {
      if (res) {
        this.user = res;
        this.featureList = this.user.feature.map(f => f.menu_name);
      }
    })
  }

  getLoggedInUser() {
    this.userName = localStorage.getItem('Username');
    this.userCharLogo = this.userName && this.getUserCharLogo(this.userName);
    const token = JSON.parse(localStorage.getItem('access_token'));
    if (token) {
      if (!this.userName) {
        this.authenticationService.getLoggedInUser().subscribe(res => {
          if (res) {
            this.userName = res.userName;
            this.userCharLogo = this.getUserCharLogo(this.userName);
            localStorage.setItem('Username', this.userName);
          }
        });
      }
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

  logout() {
    this.router.navigate(['/login']);
    localStorage.clear();
  }

  hasAccess(feature: string) {
    return !!this.featureList.find(f => f === feature);
  }

}
