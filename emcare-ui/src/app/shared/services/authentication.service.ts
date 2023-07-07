import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { JwtHelperService } from '@auth0/angular-jwt';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  backendURL = `${environment.apiUrl}`;
  userInfo = new BehaviorSubject(null);
  jwtHelper = new JwtHelperService();
  userKey = 'sample-login-page';

  private isLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(
    false
  );
  private features: BehaviorSubject<string[]> = new BehaviorSubject<string[]>(
    []
  );

  constructor(private readonly http: HttpClient) {
    this.loadUserInfo();
  }

  loadUserInfo() {
    let userdata = localStorage.getItem(this.userKey);
    if (userdata) {
      this.userInfo.next(userdata);
    }
  }

  signup(
    firstName: string,
    lastName: string,
    email: string,
    userName: string,
    password: string,
    facilityIds: Number,
    roleName: string,
    countryCode: string,
    phone: string
  ) {
    const user = {
      firstName,
      lastName,
      email,
      userName,
      password,
      regRequestFrom: 'web',
      facilityIds,
      roleName,
      countryCode,
      phone,
    };
    return this.http.post<any>(`${this.backendURL}/api/signup`, user).pipe(
      map((user) => {
        this.userInfo.next(user);
        return user;
      })
    );
  }

  getHeaders() {
    const headerObj = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
      }),
    };
    return headerObj;
  }

  login(username: string, password: string) {
    const url = `${this.backendURL}/api/auth/login`;
    const body = {
      username: username,
      password: password,
    };
    return this.http.post<any>(url, body);
  }

  getAccessTokenHeaderObj() {
    const accessToken = JSON.parse(localStorage.getItem('access_token'));
    const headerObj = {
      headers: new HttpHeaders({
        Authorization: `Bearer ${accessToken}`,
      }),
    };
    return headerObj;
  }

  getLoggedInUser() {    
    const url = `${this.backendURL}/api/user`;
    return this.http.get<any>(url, this.getAccessTokenHeaderObj());
  }

  deleteSession() {
    const url = `${this.backendURL}/api/auth/logout`;
    return this.http.get<any>(url, this.getAccessTokenHeaderObj());
  }

  getIsLoggedIn(): Observable<boolean> {
    return this.isLoggedIn;
  }

  setIsLoggedIn(loggedIn: boolean): void {
    this.isLoggedIn.next(loggedIn);
  }

  getFeatures(): Observable<string[]> {
    return this.features;
  }

  setFeatures(featuresRes: any) {
    let features = [];
    featuresRes.forEach((f) => {
      features.push(f.menuName);
      //  if there is submenu then it should also be pushed
      if (f.subMenu.length > 0) {
        f.subMenu.forEach((subF) => {
          features.push(subF.menuName);
        });
      }
    });
    this.features.next(features);
  }

  refreshToken() {
    const url = `${this.backendURL}/auth/realms/emcare/protocol/openid-connect/token`;
    const body = new HttpParams()
      .set('grant_type', 'password')
      .set('password', 'argusadmin')
      .set('client_id', 'emcare_client')
      .set('client_secret', '5b929983-175b-4e9f-97d2-ac97dff78ce9');
    return this.http.post<any>(url, body.toString(), this.getHeaders());
  }

  getAllRolesForSignUp() {
    const url = `${this.backendURL}/api/signup/roles`;
    const headerObj = this.getHeaders();
    return this.http.get<any>(url, headerObj);
  }

  getAllFacilitiesForSignUp() {
    const url = `${this.backendURL}/api/open/active/facility`;
    const headerObj = this.getHeaders();
    return this.http.get<any>(url, headerObj);
  }

  generateOTPFromUsername(username) {
    const url = `${this.backendURL}/api/open/forgotpassword/generateotp`;
    const body = { emailId: username };
    return this.http.post<any>(url, body);
  }

  verifyOTP(body) {
    const url = `${this.backendURL}/api/open/forgotpassword/verifyotp`;
    return this.http.post<any>(url, body);
  }

  resetPassword(body) {
    const url = `${this.backendURL}/api/open/resetpassword`;
    return this.http.put<any>(url, body);
  }

  getCurrentCountry() {
    const url = `${this.backendURL}/api/open/current/country`;
    return this.http.get<any>(url);
  }
}
