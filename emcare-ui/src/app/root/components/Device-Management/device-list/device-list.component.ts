import { Component, OnInit } from '@angular/core';
import { DeviceManagementService } from 'src/app/root/services/device-management.service';

@Component({
  selector: 'app-device-list',
  templateUrl: './device-list.component.html',
  styleUrls: ['./device-list.component.scss']
})
export class DeviceListComponent implements OnInit {

  deviceArr: any;
  filteredDevices: any;
  searchString: string;

  constructor(
    private readonly deviceManagementService: DeviceManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getDevices();
  }

  getDevices() {
    this.deviceArr = [];
    this.deviceManagementService.getAllDevices().subscribe((res) => {
      this.deviceArr = res;
      this.filteredDevices = this.deviceArr;
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

  searchFilter() {
    this.filteredDevices = this.deviceArr.filter( device => {
      return ( device.deviceUUID?.includes(this.searchString) 
      ||  device.androidVersion?.includes(this.searchString)
      ||  device.lastLoggedInUser?.includes(this.searchString));
    });
  }
}
