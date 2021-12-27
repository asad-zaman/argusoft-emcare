import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { JwtHelperService } from '@auth0/angular-jwt';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {

    userInfo = new BehaviorSubject(null);
    jwtHelper = new JwtHelperService();
    userKey = 'sample-login-page';

    constructor(private http: HttpClient, private router: Router) {
        this.loadUserInfo();
    }

    loadUserInfo() {
        let userdata = localStorage.getItem(this.userKey);
        if (userdata) {
            this.userInfo.next(userdata);
        }
    }

    signup(firstname: string, lastname: string, username: string, password: string) {
        return this.http.post<any>(`http://7907-150-129-149-210.ngrok.io/users/signup`, { firstname, lastname, username, password }, { withCredentials: true })
            .pipe(map(user => {
                this.userInfo.next(user);
                return user;
            }));
    }

    getHeaders() {
        const headerObj = {
            headers: new HttpHeaders({
                'Content-Type': 'application/x-www-form-urlencoded'
            })
        };
        return headerObj;
    }

    login(username: string, password: string) {
        const url = `http://7907-150-129-149-210.ngrok.io/auth/realms/emcare_demo/protocol/openid-connect/token`;
        const body = new HttpParams()
            .set('username', username)
            .set('password', password)
            .set('grant_type', 'password')
            .set('client_id', 'emcare_client')
            .set('client_secret', '5b929983-175b-4e9f-97d2-ac97dff78ce9');
        // return this.http.post<any>(`http:localhost:4200/users/authenticate`, { username, password }, { withCredentials: true })
        return this.http.post<any>(url, body.toString(), this.getHeaders());
    }

    getLoggedInUser() {
        const accessToken = JSON.parse(localStorage.getItem('access_token'));
        const headerObj = {
            headers: new HttpHeaders({
                'Authorization': `Bearer ${accessToken}`
            })
        };
        const url = `http://7907-150-129-149-210.ngrok.io/api/user`;
        return this.http.get<any>(url, headerObj);
    }

    logout() {
        return this.http.post<any>(`http:localhost:4200/users/revoke-token`, {}, { withCredentials: true });
    }

    refreshToken() {
        const url = `http://7907-150-129-149-210.ngrok.io/auth/realms/emcare_demo/protocol/openid-connect/token`;
        const body = new HttpParams()
            .set('grant_type', 'password')
            .set('password', 'parth@123')
            .set('client_id', 'emcare_client')
            .set('client_secret', '5b929983-175b-4e9f-97d2-ac97dff78ce9');
        return this.http.post<any>(url, body.toString(), this.getHeaders());
    }
}
