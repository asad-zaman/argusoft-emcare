import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

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
      log: ['', [Validators.required]],
      logs: [[], [Validators.required]]
    });
  }

  get getFormConfrols() {
    return this.addLogForm.controls;
  }

  saveData() {
    this.submitted = true;
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
