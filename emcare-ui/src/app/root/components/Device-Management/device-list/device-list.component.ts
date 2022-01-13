import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
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
  isAPIBusy: boolean = true;

  constructor(
    private readonly deviceManagementService: DeviceManagementService,
    private readonly toastr: ToastrService
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
      this.isAPIBusy = false;
    });
  }

  editDevice(event, deviceId) {
    const status = !event;
    this.deviceManagementService.updateDeviceStatusById(deviceId, status).subscribe(res => {
      this.toastr.success('Device updated successfully!!', 'EMCARE');
      this.getDevices();
    });
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredDevices = this.deviceArr.filter(device => {
      return (device.deviceUUID?.toLowerCase().includes(lowerCasedSearchString)
        || device.androidVersion?.toLowerCase().includes(lowerCasedSearchString)
        || device.usersResource.userName?.toLowerCase().includes(lowerCasedSearchString));
    });
  }
}
