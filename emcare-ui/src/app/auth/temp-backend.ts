import { Injectable } from '@angular/core';
import { HttpRequest, HttpResponse, HttpHandler, HttpEvent, HttpInterceptor, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { delay, mergeMap, materialize, dematerialize } from 'rxjs/operators';

const usersKey = 'sample-login-page';
const users = [];

if (users && !users.length) {
    users.push({ id: 1, username: 'admin@gmail.com', password: 'admin', firstName: 'Sample', lastName: 'User', refreshTokens: [] });
}

@Injectable()
export class TempBackendInterceptor implements HttpInterceptor {
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        const { url, method, headers, body } = request;

        return of(null)
            .pipe(mergeMap(handleRoute))
            .pipe(materialize())
            .pipe(delay(500))
            .pipe(dematerialize());

        function handleRoute() {
            switch (true) {
                case url.endsWith('/users/signup') && method === 'POST':
                    return addUser();
                case url.endsWith('/users/authenticate') && method === 'POST':
                    return authenticate();
                case url.endsWith('/users/refresh-token') && method === 'POST':
                    return refreshToken();
                case url.endsWith('/users/revoke-token') && method === 'POST':
                    return revokeToken();
                default:
                    return next.handle(request);
            }
        }

        function addUser() {
            const lastIndex = users[users.length - 1].id;
            const { firstname, lastname, username, password } = body;
            const isAlreadyAdded = users.findIndex(user => user.username === username);
            const newUser = { id: lastIndex + 1, username: username, password: password, firstName: firstname, lastName: lastname, refreshTokens: [] };

            if (isAlreadyAdded === -1) {
                users.push(newUser);
            }

            const accessToken = generateJwtToken();
            const refreshToken = generateRefreshToken();
            
            newUser.refreshTokens.push(refreshToken);
            localStorage.setItem(usersKey, JSON.stringify(newUser));
            
            localStorage.setItem("access_token", accessToken);
            localStorage.setItem("refresh_token", refreshToken);

            const accessTokenExpiry = new Date(Date.now() + 15 * 60 * 1000).toUTCString();
            localStorage.setItem("access_token_expiry", `${accessTokenExpiry}`);

            const refreshTokenExpiry = new Date(Date.now() + 30 * 60 * 1000).toUTCString();
            localStorage.setItem("refresh_token_expiry", `${refreshTokenExpiry}`);
            
            return ok({
                id: lastIndex + 1,
                username: username,
                firstName: firstname,
                lastName: lastname,
                jwtToken: accessToken
            })
        }

        function authenticate() {
            const { username, password } = body;
            const user = users.find(x => x.username === username && x.password === password);

            if (!user) return error('Username or password is incorrect');

            const accessToken = generateJwtToken();
            const refreshToken = generateRefreshToken();
            
            user.refreshTokens.push(refreshToken);
            localStorage.setItem(usersKey, JSON.stringify(user));
            
            localStorage.setItem("access_token", accessToken);
            localStorage.setItem("refresh_token", refreshToken);

            const accessTokenExpiry = new Date(Date.now() + 15 * 60 * 1000).toUTCString();
            localStorage.setItem("access_token_expiry", `${accessTokenExpiry}`);

            const refreshTokenExpiry = new Date(Date.now() + 30 * 60 * 1000).toUTCString();
            localStorage.setItem("refresh_token_expiry", `${refreshTokenExpiry}`);

            return ok({
                id: user.id,
                username: user.username,
                firstName: user.firstName,
                lastName: user.lastName,
                jwtToken: accessToken
            })
        }

        function refreshToken() {
            const refreshToken = getRefreshToken();

            if (!refreshToken) return unauthorized();

            const user = users.find((x: { refreshTokens: string | string[]; }) => x.refreshTokens.includes(refreshToken));

            if (!user) return unauthorized();

            // replace old refresh token with a new one and save
            user.refreshTokens = user.refreshTokens.filter(x => x !== refreshToken);
            user.refreshTokens.push(generateRefreshToken());
            localStorage.setItem(usersKey, JSON.stringify(user));

            return ok({
                id: user.id,
                username: user.username,
                firstName: user.firstName,
                lastName: user.lastName,
                jwtToken: generateJwtToken()
            })
        }

        function revokeToken() {
            if (!isLoggedIn()) return unauthorized();
            return ok();
        }

        function ok(body?: { id: any; username: any; firstName: any; lastName: any; jwtToken: string; }) {
            return of(new HttpResponse({ status: 200, body }))
        }

        function error(message: string) {
            return throwError({ error: { message } });
        }

        function unauthorized() {
            return throwError({ status: 401, error: { message: 'Unauthorized' } });
        }

        function isLoggedIn() {
            const userdata = localStorage.getItem(usersKey);
            if (userdata) {
                const jwtToken = JSON.parse(atob(localStorage.getItem('access_token').split('.')[1]));
                const tokenExpired = Date.now() > (jwtToken.exp * 1000);
                if (tokenExpired) return false;

                return true;
            }
            return true;
        }

        function generateJwtToken() {
            // create token that expires in 15 minutes
            const tokenPayload = { exp: Math.round(new Date(Date.now() + 15 * 60 * 1000).getTime() / 1000) }
            return `fake-jwt-token.${btoa(JSON.stringify(tokenPayload))}`;
        }

        function generateRefreshToken() {
            const token = new Date().getTime().toString();

            // add token cookie that expires in 30 minutes
            const expires = new Date(Date.now() + 30 * 60 * 1000).toUTCString();
            document.cookie = `fakeRefreshToken=${token}; expires=${expires}; path=/`;
            return token;
        }

        function getRefreshToken() {
            return (document.cookie.split(';').find(x => x.includes('fakeRefreshToken')) || '=').split('=')[1];
        }
    }
}

export let tempBackendProvider = {
    provide: HTTP_INTERCEPTORS,
    useClass: TempBackendInterceptor,
    multi: true
};