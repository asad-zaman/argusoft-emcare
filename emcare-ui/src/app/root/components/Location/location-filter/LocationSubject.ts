import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable()
export class LocationSubjects {

    private readonly cleatLocation: BehaviorSubject<any>;

    constructor() {
        this.cleatLocation = new BehaviorSubject(false);
    }

    setClearLocation(val) {
        this.cleatLocation.next(val);
    }

    getClearLocation(): Observable<any> {
        return this.cleatLocation.asObservable();
    }
}