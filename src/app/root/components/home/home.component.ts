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
    const loggedInUser = JSON.parse(localStorage.getItem('sample-login-page'));
    this.userName = `${loggedInUser.firstName} ${loggedInUser.lastName}`;
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.userInfo.next(null);
      this.router.navigate(['/login']);
      localStorage.clear();
    });
  }
}
