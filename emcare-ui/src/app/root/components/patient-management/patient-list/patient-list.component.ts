import { Component, OnInit } from "@angular/core";
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

    ngOnInit(): void {
        this.prerequisite();
    }

    constructor(
        private readonly fhirService: FhirService
    ) { }

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
            }
        });
    }

    onIndexChange(event) {
        this.currentPage = event;
        this.getPatientsByPageIndex(event - 1);
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

    getLocationId(data) {
        const selectedId = data;
        this.fhirService.getPatientByLocationId(selectedId).subscribe(res => {
            if (res) {
                this.filteredPatients = res;
            }
        })
    }
}