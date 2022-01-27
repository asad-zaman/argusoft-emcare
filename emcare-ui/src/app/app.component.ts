import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, NavigationStart, Event as NavigationEvent } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { HTTPStatus } from './auth/token-interceptor';
import { AuthenticationService } from './shared/services/authentication.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  currentUrl: string = '';
  userName: any;
  isUserDropdownOpen: boolean = true;
  isLocationDropdownOpen: boolean = false;
  userCharLogo: string;
  HTTPActivity: boolean;
  featureList: any = [];
  isLoggedIn: boolean = false;

  constructor(
    private readonly router: Router,
    private readonly authenticationService: AuthenticationService,
    private readonly httpStatus: HTTPStatus,
    private readonly cdr: ChangeDetectorRef,
    private readonly translate: TranslateService
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
    this.authenticationService.getIsLoggedIn().subscribe(result => {
      if (result) {
        this.getFeatureList();
      }
    });
    this.authenticationService.getFeatures().subscribe(result => {
      if (result) {
        this.featureList = result;
      }
    })
  }

  setDefaultLanguage(lan) {
    this.translate.use(lan);
    // the lang to use, if the lang isn't available, it will use the current loader to get them
    this.translate.setDefaultLang('english');
  }

  changeLaunguage(lIndex) {
    switch (lIndex) {
      case 1:
        this.translate.use('french');
        break;
      case 2:
        this.translate.use('english');
        break;
      case 3:
        this.translate.use('hindi');
        break;
      default:
        this.translate.use('english');
        break;
    }
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
        localStorage.setItem('language', res['language']);
        this.setDefaultLanguage(res['language']);
        this.authenticationService.setFeatures(res.feature.map(f => f.menu_name));
        this.getLoggedInUser(res);
      }
    });
  }

  // api should be called only once in page if not then it needs optimization
  getLoggedInUser(res) {
    this.userName = localStorage.getItem('Username');
    this.userCharLogo = this.userName && this.getUserCharLogo(this.userName);
    const token = JSON.parse(localStorage.getItem('access_token'));
    if (token) {
      if (!this.userName) {
        if (res) {
          this.userName = res.userName;
          this.userCharLogo = this.getUserCharLogo(this.userName);
          localStorage.setItem('Username', this.userName);
        }
      }
    }
  }

  hideCurrentDropdown(id) {
    switch (id) {
      case 1:
        this.isUserDropdownOpen = !this.isUserDropdownOpen;
        this.isLocationDropdownOpen = !this.isLocationDropdownOpen;
        break;
      case 2:
        this.isUserDropdownOpen = !this.isUserDropdownOpen;
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
