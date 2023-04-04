import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-add-log',
  templateUrl: './add-log.component.html',
  styleUrls: ['./add-log.component.scss']
})
export class AddLogComponent implements OnInit {

  addLogForm: FormGroup;
  submitted: boolean;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerrequisite();
  }

  prerrequisite() {
    this.initAddLogInputForm();
  }

  initAddLogInputForm() {
    this.addLogForm = this.formBuilder.group({
      applicationName: ['', [Validators.required]],
      applicationVersion: ['', [Validators.required]],
      logFile: ['', [Validators.required]],
      log: ['', []],
      logs: [[], [Validators.required]],
      file: ['', [Validators.required]]
    });
  }

  get getFormConfrols() {
    return this.addLogForm.controls;
  }

  onFileUploaded(event) {
    let file = event.target.files[0];
    this.addLogForm.get('file').setValue(file);
  }

  saveData() {
    this.submitted = true;
    if (this.addLogForm.valid) {
      const formData = new FormData();
      const log = {
        applicationName: this.addLogForm.get('applicationName').value,
        applicationVersion: this.addLogForm.get('applicationVersion').value,
        logs: this.addLogForm.get('logs').value
      }
      formData.append('file', this.addLogForm.get('file').value);
      formData.append('log', JSON.stringify(log));
      this.fhirService.addNewLog(formData).subscribe(res => {
        this.toasterService.showToast('success', 'Logs added successfully!', 'EMCARE!');
      }, (e) => {
        this.toasterService.showToast('error', e.errorMessage, 'EMCARE!');
      });
    }
  }

  addLog() {
    const selLog = this.addLogForm.get('log').value;
    const currLogs = this.addLogForm.get('logs').value;
    let logArr = [];
    if (currLogs.length > 0) {
      logArr = currLogs;
      //  If log is selected already then no need to push it again
      if (!logArr.includes(selLog)) {
        logArr.push(selLog);
        this.addLogForm.get('logs').setValue(logArr);
      }
    } else {
      logArr.push(selLog);
      this.addLogForm.get('logs').setValue(logArr);
    }
    this.addLogForm.get('log').setValue(null);
  }

  removeLog(log) {
    let logArr = this.addLogForm.get('logs').value;
    logArr = logArr.filter(l => l !== log);
    this.addLogForm.get('logs').setValue(logArr);
  }
}
