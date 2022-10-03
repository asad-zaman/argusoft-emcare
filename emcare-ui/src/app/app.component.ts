import { ChangeDetectorRef, Component, HostListener, OnInit, Renderer2 } from '@angular/core';
import { Router, NavigationStart, Event as NavigationEvent } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { HTTPStatus, LaunguageSubjects } from './auth/token-interceptor';
import { FhirService } from './shared';
import { AuthenticationService } from './shared/services/authentication.service';
import * as _ from 'lodash';
import { AuthGuard } from './auth/auth.guard';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  currentUrl: string = '';
  userName: any;
  isSettingDropdownOpen: boolean = false;
  isUserDropdownOpen: boolean = false;
  isPatientDropdownOpen: boolean = false;
  isLocationDropdownOpen: boolean = false;
  userCharLogo: string;
  HTTPActivity: boolean;
  featureList: any = [];
  isLoggedIn: boolean = false;
  currTranslations: any;
  rtlLaunguages = ['ar', 'he', 'ku', 'fa', 'ur'];
  featureArr = [];
  isSidebarOpen = true;
  currScreenWidth;
  featureIconObj = {
    'Users': 'bi bi-people nav-link_icon',
    'Locations': 'bi bi-cursor nav-link_icon',
    'Patients': 'bi bi-map nav-link_icon',
    'Feature': 'bi bi-diagram-3 nav-link_icon',
    'Devices': 'bi bi-hdd-network nav-link_icon',
    'Questionnaires': 'bi bi-window-dock nav-link_icon',
    'Languages': 'bi bi-calendar2-range nav-link_icon',
    'Facility': 'bi bi-columns-gap nav-link_icon',
    'Advanced settings': 'bi bi-gear nav-link_icon',
    'Dashboard': 'bi bi-house-door nav-link_icon',
    'Organizations': 'bi bi-border-outer nav-link_icon'
  }

  constructor(
    private readonly router: Router,
    private readonly authenticationService: AuthenticationService,
    private readonly httpStatus: HTTPStatus,
    private readonly cdr: ChangeDetectorRef,
    private readonly translate: TranslateService,
    private readonly fhirService: FhirService,
    private readonly lanSubjects: LaunguageSubjects,
    private readonly renderer: Renderer2,
    private readonly authGuard: AuthGuard
  ) {
    this.getScreenSize();
  }

  @HostListener('window:resize', ['$event'])
  getScreenSize(event?) {
    this.currScreenWidth = window.screen.width;
    this.applySidebarChange();
  }

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
        this.setLoggedInUserData();
      }
    });
    this.authenticationService.getFeatures().subscribe(result => {
      if (result && result.length > 0) {
        this.featureList = result;
      } else {
        /*  on refreshing the page behaviour subject will be lost
          so resetting the feature array to behaviour subject to render the sidebar again */
        let userFeatures = localStorage.getItem('userFeatures');
        if (userFeatures) {
          this.authenticationService.setFeatures(this.featureArr);
        }
      }
    });
    this.detectLanChange();
  }

  detectLanChange() {
    this.lanSubjects.getLaunguage().subscribe(lan => {
      if (Object.keys(lan).length !== 0) {
        this.lanSubjects.getCurrentTranslation().subscribe(res => {
          if (Object.keys(res).length !== 0) {
            this.translate.setTranslation(lan, JSON.parse(res), true);
            this.translate.use(lan);
          }
        });
        this.checkRTLLaunguage();
      }
    });
  }

  getAllLaunguages() {
    const currLan = localStorage.getItem('language');
    this.fhirService.getAllLaunguagesTranslations().subscribe(res => {
      if (res) {
        _.forIn(res, (value, _key) => {
          if (value.languageCode === currLan) {
            this.currTranslations = JSON.parse(value.languageData);
            this.translate.setTranslation(currLan, this.currTranslations, true);
            this.translate.use(currLan);
          }
        });
      }
    });
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
      }
    });
  }

  getUserCharLogo(username) {
    return username.substring(0, 1).toUpperCase();
  }

  setLoggedInUserData() {
    let userFeatures = localStorage.getItem('userFeatures');
    if (userFeatures) {
      this.featureArr = JSON.parse(userFeatures)['feature'];
      if (this.featureArr) {
        this.featureArr.map(f => {
          f['subMenuActive'] = false;
          if (f.subMenu.length > 0) {
            f['dropdownValue'] = false;
          }
        });
      }
      this.checkRTLLaunguage();
      this.setUserDetails();
      this.getAllLaunguages();
    }
  }

  setFeatureSubMenuFalse() {
    this.featureArr.map(f => {
      f['subMenuActive'] = false;
    });
  }

  setFeatureDropdownFalse(feature) {
    this.featureArr.map(f => {
      if (f.subMenu.length > 0 && f !== feature) {
        f['dropdownValue'] = false;
      }
    });
  }

  setUserDetails() {
    this.userName = localStorage.getItem('Username');
    this.userCharLogo = this.userName && this.getUserCharLogo(this.userName);
  }

  hideCurrentDropdown(id) {
    switch (id) {
      case 1:
        this.isUserDropdownOpen = !this.isUserDropdownOpen;
        break;
      case 2:
        this.isLocationDropdownOpen = !this.isLocationDropdownOpen;
        break;
      case 3:
        this.isPatientDropdownOpen = !this.isPatientDropdownOpen;
        break;
      case 4:
        this.isSettingDropdownOpen = !this.isSettingDropdownOpen;
        break;
    }
  }

  logout() {
    //  on logout direction should be set to ltr as it's english language
    this.renderer.setAttribute(document.body, 'dir', 'ltr');
    this.router.navigate(['/login']);
    localStorage.clear();
  }

  hasAccess(feature: string) {
    return this.featureList.find(f => f === feature);
  }

  checkRTLLaunguage() {
    const lan = localStorage.getItem('language');
    const isRTL = this.rtlLaunguages.indexOf(lan) !== -1;
    if (isRTL) {
      this.renderer.setAttribute(document.body, 'dir', 'rtl');
    } else {
      this.renderer.setAttribute(document.body, 'dir', 'ltr');
    }
  }

  checkCurrentUrlAndShowHeaderBar() {
    const arr = ['/', '/login', '/signup', '/forgotPassword'];
    return arr.includes(this.currentUrl);
  }

  navigateToDashboard() {
    this.router.navigate(['/dashboard']);
  }

  changeDropdownValueOrRoute(feature, isSubmenu = false) {
    if (isSubmenu) {
      const route = this.authGuard.getFeatureAndRedirectUser(feature)[0];
      this.router.navigate([`${route}`]);
      this.applySidebarChange();
    } else {
      if (feature.hasOwnProperty('dropdownValue')) {
        this.setFeatureDropdownFalse(feature);
        feature['dropdownValue'] = !feature['dropdownValue'];
      } else {
        this.setFeatureDropdownFalse(feature);
        const route = this.authGuard.getFeatureAndRedirectUser(feature.menuName)[0];
        this.router.navigate([`${route}`]);
        this.applySidebarChange();
      }
    }
    this.setFeatureSubMenuFalse();
  }

  getIconClassFromFeatureName(fName) {
    return this.featureIconObj[fName];
  }

  checkIsActiveOrNot(feature, featureName, subMenu = false) {
    if (subMenu) {
      feature['subMenuActive'] = true;
    }
    const routeArr = this.authGuard.getFeatureAndRedirectUser(featureName);
    return routeArr.includes(this.currentUrl);
  }

  changeSidebarVar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  applySidebarChange() {
    if (window.screen.width < 992) {
      this.isSidebarOpen = false;
    } else {
      this.isSidebarOpen = true;
    }
  }
}
