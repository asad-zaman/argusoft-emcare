import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthGuard } from 'src/app/auth/auth.guard';
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
  isView: boolean = true;
  isAllowed: boolean = true;

  constructor(
    private readonly deviceManagementService: DeviceManagementService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard,
    private readonly translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getDevicesByPageIndex(this.currentPage);
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  manipulateResponse(res) {
    if (res && res['list']) {
      this.deviceArr = this.mapToggleButtonData(res['list']);
      this.filteredDevices = this.deviceArr;
      this.totalCount = res['totalCount'];
      this.isAPIBusy = false;
    }
  }

  mapToggleButtonData(data) {
    data.forEach(el => {
      el['appUsage'] = !el.isBlocked;
    });
    return data;
  }

  getDevicesByPageIndex(index) {
    this.deviceArr = [];
    this.deviceManagementService.getDevicesByPageIndex(index).subscribe(res => {
      this.manipulateResponse(res);
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    if (this.searchString && this.searchString.length >= 1) {
      this.deviceArr = [];
      this.deviceManagementService.getDevicesByPageIndex(event - 1, this.searchString).subscribe(res => {
        this.manipulateResponse(res);
      });
    } else {
      this.getDevicesByPageIndex(event - 1);
    }
  }

  editDevice(event, deviceId) {
    const status = !event.target.checked;
    this.deviceManagementService.updateDeviceStatusById(deviceId, status).subscribe(res => {
      this.toasterService.showToast('success', 'Device updated successfully!', 'EMCARE');
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

  getLabel(index) {
    const key = index === 0 ? 'Previous' : 'Next';
    let tr;
    this.translate.get(key).subscribe(res => {
      tr = res;
    });
    return tr;
  }
}
