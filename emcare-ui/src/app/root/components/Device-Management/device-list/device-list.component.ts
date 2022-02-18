import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
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
  searchTermChanged: Subject<string> = new Subject<string>();

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

  manipulateResponse(res) {
    if (res && res['list']) {
      this.deviceArr = res['list'];
      this.filteredDevices = this.deviceArr;
      this.totalCount = res['totalCount'];
      this.isAPIBusy = false;
    }
  }
  
  getDevicesByPageIndex(index) {
    this.deviceArr = [];
    this.deviceManagementService.getDevicesByPageIndex(index).subscribe(res => {
      this.manipulateResponse(res);
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
    this.resetCurrentPage();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {
          this.deviceArr = [];
          this.deviceManagementService.getDevicesByPageIndex(this.currentPage, this.searchString).subscribe(res => {
            this.manipulateResponse(res);
          });
        } else {
          this.getDevicesByPageIndex(this.currentPage);
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }
}
