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
        return this.http.post<any>(`http:localhost:4200/users/signup`, { firstname, lastname, username, password }, { withCredentials: true })
            .pipe(map(user => {
                this.userInfo.next(user);
                return user;
            }));
    }

    getHeaders() {
        const headerObj = {
            headers: new HttpHeaders({
                'Content-Type': 'application/x-www-form-urlencoded',
                'Access-Control-Allow-Origin': '*'
            })
        };
        return headerObj;
    }

    login(username: string, password: string) {
        const url = `http://782c-14-192-29-30.ngrok.io/auth/realms/emcare/protocol/openid-connect/token`;
        const body = new HttpParams()
            .set('username', 'user1')
            .set('password', 'argusadmin')
            .set('grant_type', 'password')
            .set('client_id', 'login-app')
            .set('client_secret', '50fe2579-ea20-4cf2-b0d2-e219f67dfbb4');
        // return this.http.post<any>(`http:localhost:4200/users/authenticate`, { username, password }, { withCredentials: true })
        return this.http.post<any>(url, body.toString(), this.getHeaders())
            .pipe(map(user => {
                this.userInfo.next(user);
                return user;
            }));
    }

    logout() {
        return this.http.post<any>(`http:localhost:4200/users/revoke-token`, {}, { withCredentials: true });
    }

    refreshToken() {
        return this.http.post<any>(`http:localhost:4200/users/refresh-token`, {}, { withCredentials: true })
            .pipe(map((user) => {
                return user;
            }));
    }
}
