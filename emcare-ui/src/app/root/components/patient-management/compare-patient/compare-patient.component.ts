import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ToasterService } from "src/app/shared";
import { FhirService } from "src/app/shared/services/fhir.service";

@Component({
    selector: 'app-compare-patient',
    templateUrl: './compare-patient.component.html',
    styleUrls: ['./compare-patient.component.scss']
})
export class ComparePatientComponent implements OnInit {

    comparePatientForm: FormGroup;
    submitted = false;
    patientJSON: string = '';
  
    constructor(
      private readonly formBuilder: FormBuilder,
      private readonly router: Router,
      private readonly fhirService: FhirService,
      private readonly toasterService: ToasterService
    ) { }
  
    ngOnInit(): void {
      this.prerequisite();
    }
  
    prerequisite() {
      this.initForm();
    }
  
    initForm() {
      this.comparePatientForm = this.formBuilder.group({
        firstPatient: [''],
        secondPatient: [''],
        firstPatientDetails: ['', [Validators.required]],
        secondPatientDetails: ['', [Validators.required]],
      });
    }
  
    get f() {
      return this.comparePatientForm.controls;
    }

    onFileUploaded(event) {
      let elementName = (event.target as HTMLInputElement).id;
      let file = event.target.files[0];
      let fileReader: FileReader = new FileReader();
      let self = this;
      fileReader.onloadend = function(x) {
        self.patientJSON = fileReader.result as string;
        self.comparePatientForm.get(elementName + "Details").setValue(self.patientJSON);
      }
      fileReader.readAsText(file);
    }
  
    comparePatients() {
      this.submitted = true;
      if (this.comparePatientForm.valid) {
        const data = [
          this.comparePatientForm.get("firstPatientDetails").value,
          this.comparePatientForm.get("secondPatientDetails").value
        ];
        this.fhirService.comparePatients(data).subscribe(res => {
          if(res == true){
            this.toasterService.showInfo("Duplicate Patients Detected");
          } else  if (res == false) {
            this.toasterService.showSuccess("Unique Patients Detected");
          } else {
            this.toasterService.showWarning("Cannot determine Duplication status");
          }
        }, err => {
          this.toasterService.showError("Error encoutnered, please check Patient data.");
        });
      }
    }

}