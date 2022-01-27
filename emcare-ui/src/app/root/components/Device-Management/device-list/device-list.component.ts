import { Component, OnInit } from '@angular/core';
import { DeviceManagementService } from 'src/app/root/services/device-management.service';
import { ToasterService } from 'src/app/shared';

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
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;

  constructor(
    private readonly deviceManagementService: DeviceManagementService,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getDevicesByPageIndex(this.currentPage);
  }

  getDevicesByPageIndex(index) {
    this.deviceArr = [];
    this.deviceManagementService.getDevicesByPageIndex(index).subscribe(res => {
      if (res && res['list']) {
        this.deviceArr = res['list'];
        this.filteredDevices = this.deviceArr;
        this.totalCount = res['totalCount'];
        this.isAPIBusy = false;
      }
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    this.getDevicesByPageIndex(event - 1);
  }

  editDevice(event, deviceId) {
    const status = !event;
    this.deviceManagementService.updateDeviceStatusById(deviceId, status).subscribe(res => {
      this.toasterService.showSuccess('Device updated successfully!', 'EMCARE');
      this.resetCurrentPage();
      this.getDevicesByPageIndex(this.currentPage);
    });
  }

  resetCurrentPage() {
    this.currentPage = 0;
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
