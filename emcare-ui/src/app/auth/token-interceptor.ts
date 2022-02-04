import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
@Injectable()
export class LaunguageSubjects {

    private readonly frenchTranslations: BehaviorSubject<any>;
    private readonly hinTranslations: BehaviorSubject<any>;
    private readonly currentLanguage: BehaviorSubject<any>;

    constructor() {
        this.frenchTranslations = new BehaviorSubject({});
        this.hinTranslations = new BehaviorSubject({});
        this.currentLanguage = new BehaviorSubject({});
    }

    setFrenchTranslations(frenchObject: any) {
        this.frenchTranslations.next(frenchObject);
    }

    setHindiTranslations(hinObject: any) {
        this.hinTranslations.next(hinObject);
    }

    getFrenchTranslations(): Observable<any> {
        return this.frenchTranslations.asObservable();
    }

    getHindiTranslations(): Observable<any> {
        return this.hinTranslations.asObservable();
    }

    setLaunguage(lan: any) {
        this.currentLanguage.next(lan);
    }

    getLaunguage(): Observable<any> {
        return this.currentLanguage.asObservable();
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
        private readonly status: HTTPStatus
    ) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        this.status.setHttpStatus(true);
        return next.handle(request).pipe(
            catchError((error: any) => {
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