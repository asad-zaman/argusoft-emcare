import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { JwtHelperService } from '@auth0/angular-jwt';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {

    backendURL = `${environment.apiUrl}`;
    userInfo = new BehaviorSubject(null);
    jwtHelper = new JwtHelperService();
    userKey = 'sample-login-page';
    private isLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
    private features: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([]);

    constructor(private http: HttpClient, private router: Router) {
        this.loadUserInfo();
    }

    loadUserInfo() {
        let userdata = localStorage.getItem(this.userKey);
        if (userdata) {
            this.userInfo.next(userdata);
        }
    }

    signup(firstName: string, lastName: string, email: string, password: string, locationId: Number, roleName: string) {
        const user = {
            firstName,
            lastName,
            email,
            password,
            regRequestFrom: 'web',
            locationId,
            roleName
        }
        return this.http.post<any>(`${this.backendURL}/api/signup`, user)
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
        const url = `${this.backendURL}/auth/realms/emcare/protocol/openid-connect/token`;
        const body = new HttpParams()
            .set('username', username)
            .set('password', password)
            .set('grant_type', 'password')
            .set('client_id', 'emcare')
            .set('client_secret', 'b5a37bde-8d54-4837-a8dc-12e1f808e26e');
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
        const url = `${this.backendURL}/api/user`;
        return this.http.get<any>(url, headerObj);
    }

    logout() {
        return this.http.post<any>(`http:localhost:4200/users/revoke-token`, {}, { withCredentials: true });
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

    setFeatures(featuresRes: any): void {
        const features = featuresRes.feature.map(f => f.menu_name)
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

    getAllLocationsForSignUp() {
        const url = `${this.backendURL}/api/signup/location`;
        const headerObj = this.getHeaders();
        return this.http.get<any>(url, headerObj);
    }
}
