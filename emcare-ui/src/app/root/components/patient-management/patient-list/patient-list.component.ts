import { Component, OnInit } from "@angular/core";
import { FhirService } from "src/app/shared/services/fhir.service";

@Component({
    selector: 'app-patient-list',
    templateUrl: './patient-list.component.html',
    styleUrls: ['./patient-list.component.scss']
})
export class PatientListComponent implements OnInit {

    patients: any
    patientDetails: any
    showPatientDetailsFlag: boolean = false

    ngOnInit(): void {
        this.prerequisite();
    }

    constructor(
        private readonly fhirService: FhirService
    ) { }

    prerequisite() {
        this.getPatients();
    }

    getPatients() {
        this.fhirService.getAllPatients().subscribe(res => {
            if(res) {
                this.patients = res;
            }
        });
    }

    showPatientDetails(id){
        this.fhirService.getPatientById(id).subscribe(res => {
            if(res) {
                this.patientDetails = res;
                this.showPatientDetailsFlag = true;
            }
        });
    }

    closePopup(){
        this.showPatientDetailsFlag = false;
        this.patientDetails = null;
    }
}