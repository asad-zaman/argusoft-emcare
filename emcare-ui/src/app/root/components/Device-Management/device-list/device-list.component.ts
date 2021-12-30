import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DeviceManagementService } from 'src/app/root/services/device-management.service';

@Component({
  selector: 'app-device-list',
  templateUrl: './device-list.component.html',
  styleUrls: ['./device-list.component.scss']
})
export class DeviceListComponent implements OnInit {

  deviceArr: any;

  constructor(
    private readonly router: Router,
    private readonly deviceManagementService: DeviceManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getDevices();
  }

  getDevices() {
    this.deviceManagementService.getAllDevices().subscribe((res) => {
      this.deviceArr = res;
    })
  }

  editDevice(event, data) {
    const obj = {
      "deviceId": data['deviceId'],
      "deviceUUID": data['deviceUUID'],
      "androidVersion": data['androidVersion'],
      "lastLoggedInUser": data['lastLoggedInUser'],
      "isBlocked": event
    }
    this.deviceManagementService.updateDeviceById(obj).subscribe(res => {
      this.getDevices();
    });
  }
}
