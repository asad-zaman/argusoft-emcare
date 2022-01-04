import { Component, OnInit } from '@angular/core';
import { Router, NavigationStart, Event as NavigationEvent } from '@angular/router';
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

  constructor(
    private readonly router: Router,
    private readonly authenticationService: AuthenticationService
  ) { }

  ngOnInit() {
    this.prerequisite();
  }

  prerequisite() {
    this.getCurrentPage();
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
    this.authenticationService.getLoggedInUser().subscribe(res => {
      if (res) {
        const userNameArr = res.userName.split(' ');
        this.userName = res.userName;
        this.userCharLogo = `${userNameArr[0].toString().charAt(0).toUpperCase()}${userNameArr[1].toString().charAt(0).toUpperCase()}`;
        console.log(this.userCharLogo);
      }
    });
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
