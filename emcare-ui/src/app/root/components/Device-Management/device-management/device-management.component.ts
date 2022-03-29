import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-device-management',
  templateUrl: './device-management.component.html',
  styleUrls: ['./device-management.component.scss']
})
export class DeviceManagementComponent implements OnInit {

  deviceForm: FormGroup;
  isEdit: boolean;
  editId: string;
  deviceArr = [];

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.iniDeviceInputForm();
    this.setDeviceArr();
    this.checkEditParam();
  }

  setDeviceArr() {
    const data = localStorage.getItem('deviceArr');
    if (data) {
      this.deviceArr = JSON.parse(data);
    } else {
      this.deviceArr = [];
    }
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.setCurrentDevice(this.editId);
    }
  }

  setCurrentDevice(index) {
    const data = this.deviceArr[index];
    this.deviceForm.setValue(data);
  }

  iniDeviceInputForm() {
    this.deviceForm = this.formBuilder.group({
      imei: ['', [Validators.required, Validators.maxLength(14)]],
      macAddress: ['', [Validators.required, Validators.maxLength(12)]],
      androidVersion: ['', [Validators.required]],
      user: ['', [Validators.required]],
      isBlocked: [false, [Validators.required]]
    });
  }

  saveData() {
    if (this.deviceForm.valid) {
      const obj = this.deviceForm.value;
      if (this.isEdit) {
        this.deviceArr[this.editId] = obj;
      } else {
        this.deviceArr.push(obj);
      }
      localStorage.setItem('deviceArr', JSON.stringify(this.deviceArr));
      this.showDevices();
      // const data = {
      //   "imei": this.deviceForm.get('imei').value,
      //   "macAddress": this.deviceForm.get('macAddress').value,
      //   "androidVersion": this.deviceForm.get('androidVersion').value
      //   "user": this.deviceForm.get('user').value
      //   "isBlocked": this.deviceForm.get('isBlocked').value
      // };
    }
  }

  showDevices() {
    this.router.navigate([`showDevices`]);
  }
}
