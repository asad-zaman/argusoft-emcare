import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { JwtHelperService } from '@auth0/angular-jwt';
import { HttpClient } from '@angular/common/http';
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

    login(username: string, password: string) {
        return this.http.post<any>(`http:localhost:4200/users/authenticate`, { username, password }, { withCredentials: true })
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
