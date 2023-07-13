import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AuthGuard } from "src/app/auth/auth.guard";
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
  isAdd: boolean = true;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authGuard: AuthGuard,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.initForm();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAdd = res.featureJSON['canAdd'];
      }
    });
  }

  initForm() {
    this.comparePatientForm = this.formBuilder.group({
      firstPatient: ['', [Validators.required]],
      secondPatient: ['', [Validators.required]],
      firstPatientDetails: ['', [Validators.required]],
      secondPatientDetails: ['', [Validators.required]],
    });
  }

  get getFormConfrols() {
    return this.comparePatientForm.controls;
  }

  onFileUploaded(event) {
    let elementName = (event.target as HTMLInputElement).id;
    let file = event.target.files[0];
    let fileReader: FileReader = new FileReader();
    let self = this;
    fileReader.onloadend = function (x) {
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
        if (res == true) {
          this.toasterService.showToast('info', "Duplicate Patients Detected", 'EMCARE');
        } else if (res == false) {
          this.toasterService.showToast('success', "Unique Patients Detected", 'EMCARE');
        } else {
          this.toasterService.showToast('warn', "Cannot determine Duplication status", 'EMCARE');
        }
      }, err => {
        this.toasterService.showToast('error', "Error encountered, please check Patient data.", 'EMCARE');
      });
    }
  }

}