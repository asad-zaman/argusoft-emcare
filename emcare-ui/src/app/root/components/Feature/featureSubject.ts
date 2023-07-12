import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable()
export class FeatureSubjects {

    private readonly isActionShow: BehaviorSubject<any>;

    constructor() {
        this.isActionShow = new BehaviorSubject(false);
    }

    setActionShow(val) {
        this.isActionShow.next(val);
    }

    getActionShow(): Observable<any> {
        return this.isActionShow.asObservable();
    }
}