import { Component, OnInit } from "@angular/core";
import { Subject } from "rxjs";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
import { AuthGuard } from "src/app/auth/auth.guard";
import { ToasterService } from "src/app/shared";
import { FhirService } from "src/app/shared/services/fhir.service";

@Component({
    selector: 'app-patient-list',
    templateUrl: './patient-list.component.html',
    styleUrls: ['./patient-list.component.scss']
})
export class PatientListComponent implements OnInit {

    patients: any
    filteredPatients: any;
    searchString: string;
    patientDetails: any
    showPatientDetailsFlag: boolean = false
    currentPage = 0;
    totalCount = 0;
    tableSize = 10;
    isAPIBusy: boolean = true;
    isLocationFilterOn: boolean = false;
    selectedId: any;
    searchTermChanged: Subject<string> = new Subject<string>();
    isView: boolean = true;

    constructor(
        private readonly fhirService: FhirService,
        private readonly toasterService: ToasterService,
        private readonly authGuard: AuthGuard
    ) { }

    ngOnInit(): void {
        this.prerequisite();
    }

    prerequisite() {
        this.checkFeatures();
        this.getPatientsByPageIndex(this.currentPage);
    }

    checkFeatures() {
        this.authGuard.getFeatureData().subscribe(res => {
            if (res.relatedFeature && res.relatedFeature.length > 0) {
                this.isView = res.featureJSON['canView'];
            }
        });
    }

    manipulateResponse(res) {
        if (res && res['list']) {
            this.patients = res['list'];
            this.filteredPatients = this.patients;
            this.totalCount = res['totalCount'];
            this.isAPIBusy = false;
        }
    }

    getPatientsByPageIndex(index) {
        this.patients = [];
        this.fhirService.getPatientsByPageIndex(index).subscribe(res => {
            this.manipulateResponse(res);
        });
    }

    getPatientsBasedOnLocationAndPageIndex(pageIndex) {
        this.fhirService.getPatientsByLocationAndPageIndex(this.selectedId, pageIndex).subscribe(res => {
            if (res) {
                this.filteredPatients = [];
                this.filteredPatients = res['list'];
                this.totalCount = res['totalCount'];
                this.isAPIBusy = false;
            }
        });
    }

    onIndexChange(event) {
        this.currentPage = event;
        if (this.isLocationFilterOn) {
            this.getPatientsBasedOnLocationAndPageIndex(event - 1);
        } else {
            this.getPatientsByPageIndex(event - 1);
        }
    }

    showPatientDetails(id) {
        this.fhirService.getPatientById(id).subscribe(res => {
            if (res) {
                this.patientDetails = res;
                this.showPatientDetailsFlag = true;
            }
        });
    }

    closePopup() {
        this.showPatientDetailsFlag = false;
        this.patientDetails = null;
    }

    searchFilter() {
        this.resetPageIndex();
        if (this.searchTermChanged.observers.length === 0) {
            this.searchTermChanged.pipe(
                debounceTime(1000),
                distinctUntilChanged()
            ).subscribe(_term => {
                if (this.searchString && this.searchString.length >= 1) {
                    this.patients = [];
                    this.fhirService.getPatientsByPageIndex(this.currentPage, this.searchString).subscribe(res => {
                        this.manipulateResponse(res);
                    });
                } else {
                    if (this.isLocationFilterOn) {
                        this.getPatientsBasedOnLocationAndPageIndex(this.currentPage);
                    } else {
                        this.getPatientsByPageIndex(this.currentPage);
                    }
                }
            });
        }
        this.searchTermChanged.next(this.searchString);
    }

    resetPageIndex() {
        this.currentPage = 0;
    }

    getLocationId(data) {
        this.selectedId = data;
        if (this.selectedId) {
            this.isLocationFilterOn = true;
            this.resetPageIndex();
            const pageIndex = this.currentPage == 0 ? this.currentPage : this.currentPage - 1;
            this.getPatientsBasedOnLocationAndPageIndex(pageIndex);
        } else {
            this.toasterService.showToast('info', 'Please select Location!', 'EMCARE');
        }
    }
}