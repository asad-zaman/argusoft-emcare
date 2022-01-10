import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService } from 'src/app/shared';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string | undefined;
  error = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
      password: ['', Validators.required]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  userLogin() {
    this.submitted = true;
    // stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }
    this.authService.login(this.loginForm.value.username, this.loginForm.value.password)
      .subscribe(
        data => {
          if (data) {
            const tokenexpiration: Date = new Date();
            tokenexpiration.setSeconds(new Date().getSeconds() + data.expires_in);
            localStorage.setItem('access_token', JSON.stringify(data.access_token));
            localStorage.setItem('access_token_expiry_time', JSON.stringify(tokenexpiration));
            
            const refreshTokenexpiration: Date = new Date();
            refreshTokenexpiration.setSeconds(new Date().getSeconds() + data.refresh_expires_in);
            localStorage.setItem('refresh_token', JSON.stringify(data.refresh_token));
            localStorage.setItem('refresh_token_expiry_time', JSON.stringify(refreshTokenexpiration));
            this.router.navigate(["/showUsers"]);
            this.authService.setIsLoggedIn(true);
          }
        },
        error => {
          this.error = error['error']['message'];
          this.loading = false;
          this.authService.setIsLoggedIn(false);
        });
  }

  navigateToSignup() {
    this.router.navigate(["/signup"]);
  }
}
