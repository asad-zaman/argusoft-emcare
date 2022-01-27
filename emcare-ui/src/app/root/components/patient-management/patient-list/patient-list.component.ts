import { Component, OnInit } from "@angular/core";
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

    constructor(
        private readonly fhirService: FhirService,
        private readonly toasterService: ToasterService
    ) { }

    ngOnInit(): void {
        this.prerequisite();
    }

    prerequisite() {
        this.getPatientsByPageIndex(this.currentPage);
    }

    getPatientsByPageIndex(index) {
        this.patients = [];
        this.fhirService.getPatientsByPageIndex(index).subscribe(res => {
            if (res && res['list']) {
                this.patients = res['list'];
                this.filteredPatients = this.patients;
                this.totalCount = res['totalCount'];
                this.isAPIBusy = false;
            }
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
        if(this.isLocationFilterOn) {
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
        const lowerCasedSearchString = this.searchString?.toLowerCase();
        this.filteredPatients = this.patients.filter(patient => {
            return (patient.identifier?.toLowerCase().includes(lowerCasedSearchString)
                || patient.givenName?.toLowerCase().includes(lowerCasedSearchString)
                || patient.familyName?.toLowerCase().includes(lowerCasedSearchString)
                || patient.gender?.toLowerCase().includes(lowerCasedSearchString)
                || patient.caregiver?.toLowerCase().includes(lowerCasedSearchString)
                || patient.location?.toLowerCase().includes(lowerCasedSearchString));
        });
    }

    resetPageIndex() {
        this.currentPage = 0;
    }

    getLocationId(data) {
        this.selectedId = data;
        if(this.selectedId) {
            this.isLocationFilterOn = true;
            this.resetPageIndex();
            const pageIndex = this.currentPage == 0 ? this.currentPage : this.currentPage - 1;
            this.getPatientsBasedOnLocationAndPageIndex(pageIndex);
        } else {
            this.toasterService.showInfo('Please select Location!', 'EMCARE');
        }
    }
}