import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService, FhirService, ToasterService } from 'src/app/shared';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { appConstants } from 'src/app/app.config';
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
  countryData;
  downloadURL;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthenticationService,
    private readonly router: Router,
    private readonly toasterService: ToasterService,
    private readonly fhirService: FhirService
  ) { }

  ngOnInit() {
    //  only for developement purpose
    const url = environment.testUrl;
    this.loginForm = this.formBuilder.group({
      username: [window.location.href == url ? environment.testUsername : '', [Validators.required, 
        Validators.pattern(appConstants.emailPattern)]],
      password: [window.location.href == url ? environment.testPassword : '', Validators.required]
    });
    this.getCountry();
  }

  getCountry() {
    this.fhirService.getCountry().subscribe(res => {
      this.countryData = res;
      this.downloadURL = `${window.origin}/${this.countryData.url}`;
    });
  }

  get getFormConfrols() {
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
            localStorage.setItem(appConstants.localStorageKeys.accessToken, JSON.stringify(data.access_token));
            localStorage.setItem(appConstants.localStorageKeys.accessTokenExpiryTime, JSON.stringify(tokenexpiration));

            const refreshTokenexpiration: Date = new Date();
            refreshTokenexpiration.setSeconds(new Date().getSeconds() + data.refresh_expires_in);
            localStorage.setItem(appConstants.localStorageKeys.refreshToken, JSON.stringify(data.refresh_token));
            localStorage.setItem(appConstants.localStorageKeys.refreshTokenExpiryTime, JSON.stringify(refreshTokenexpiration));

            this.getLoggedInUserData();
          }
        },
        error => {
          this.error = error.error['error_description'];
          this.loading = false;
          this.toasterService.showToast('error', this.error, 'EmCare');
          this.authService.setIsLoggedIn(false);
        });
  }

  navigateToSignup() {
    this.router.navigate(["/signup"]);
  }

  navigateToForgotPassword() {
    this.router.navigate(["/forgotPassword"]);
  }

  getLoggedInUserData() {
    this.authService.getLoggedInUser().subscribe(res => {
      if (res) {
        const featureObj = { feature: res['feature'] };
        localStorage.setItem(appConstants.localStorageKeys.userFeatures, JSON.stringify(featureObj));
        localStorage.setItem(appConstants.localStorageKeys.language, res['language']);
        localStorage.setItem(appConstants.localStorageKeys.Username, res.userName);
        this.authService.setFeatures(res['feature']);
        this.router.navigate(["/dashboard"]);
        this.toasterService.showToast('success', 'Welcome to EmCare!', 'EMCARE');
        this.authService.setIsLoggedIn(true);
      }
    });
  }

  getDownloadURL() {
    console.log(`${window.origin}/${this.countryData.url}`);
    return `${window.origin}/${this.countryData.url}`;
  }
}
