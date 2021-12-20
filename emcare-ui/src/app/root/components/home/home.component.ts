import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { first } from 'rxjs/operators';
import { AuthenticationService } from 'src/app/shared';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  userName: string = '';

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // const loggedInUser = JSON.parse(localStorage.getItem('sample-login-page'));
    // this.userName = `${loggedInUser.firstName} ${loggedInUser.lastName}`;
    this.getLoggedInUser();
  }

  getLoggedInUser() {
    this.authService.getLoggedInUser().subscribe(res => {
      console.log(res);
    })
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.userInfo.next(null);
      this.router.navigate(['/login']);
      localStorage.clear();
    });
  }

  // addPatients() {
  //   this.router.navigate(['/addPatient']);
  // }

  // addOrganization() {
  //   this.router.navigate(['/addOrganization']);
  // }

  // showPatients() {
  //   this.router.navigate(['/showPatient']);
  // }

  // showOrganizations() {
  //   this.router.navigate(['/showOrganization']);
  // }

  addLocationType() {
    this.router.navigate(['/addLocationType']);
  }

  showLocationType() {
    this.router.navigate(['/showLocationType']);
  }

  addLocation() {
    this.router.navigate(['/addLocation']);
  }

  showLocation() {
    this.router.navigate(['/showLocation']);
  }

  showPatients() {
    this.router.navigate(['/showPatients']);
  }

  addRole() {
    this.router.navigate(['/addRole']);
  }

  showRoles() {
    this.router.navigate(['/showRoles']);
  }

}
