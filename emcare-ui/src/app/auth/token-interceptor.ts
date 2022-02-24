import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
@Injectable()
export class LaunguageSubjects {

    private readonly currentLanguage: BehaviorSubject<any>;
    private readonly currentTranslations: BehaviorSubject<any>;

    constructor() {
        this.currentLanguage = new BehaviorSubject({});
        this.currentTranslations = new BehaviorSubject({});
    }

    setLaunguage(lan: any) {
        this.currentLanguage.next(lan);
    }

    getLaunguage(): Observable<any> {
        return this.currentLanguage.asObservable();
    }

    setCurrentTranslation(lan: any) {
        this.currentTranslations.next(lan);
    }

    getCurrentTranslation(): Observable<any> {
        return this.currentTranslations.asObservable();
    }
}
@Injectable()
export class HTTPStatus {

    asyncCall = false;
    private readonly requestInFlight$: BehaviorSubject<boolean>;

    constructor() {
        this.requestInFlight$ = new BehaviorSubject(false);
    }

    setAsyncCall(val) {
        this.asyncCall = val;
    }

    getAsyncCall() {
        return this.asyncCall;
    }

    setHttpStatus(inFlight: boolean) {
        this.requestInFlight$.next(inFlight);
    }

    getHttpStatus(): Observable<boolean> {
        return this.requestInFlight$.asObservable();
    }
}
@Injectable()
export class TokenInterceptor implements HttpInterceptor {

    constructor(
        private readonly status: HTTPStatus,
        private readonly router: Router
    ) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        this.status.setHttpStatus(true);
        return next.handle(request).pipe(
            catchError((error: any) => {
                //  when opening an application after long time token was expired so apis are giving 401 error
                //  so user needs to be logout
                if (error.status === 401) {
                    this.router.navigate(['/login']);
                    localStorage.clear();
                }
                return throwError(error);
            }),
            finalize(() => {
                this.decreaseRequests();
            })
        );
    }

    private decreaseRequests() {
        this.status.setHttpStatus(false);
    }
}