import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Router } from '@angular/router';
import { LocationService } from 'src/app/root/services/location.service';
import { ToasterService } from 'src/app/shared';
import { AuthGuard } from 'src/app/auth/auth.guard';
@Component({
  selector: 'app-show-location',
  templateUrl: './show-location.component.html',
  styleUrls: ['./show-location.component.scss']
})
export class ShowLocationComponent implements OnInit {

  filteredLocations: any;
  locationArr: any;
  searchString: string;
  isAPIBusy: boolean = true;
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  searchTermChanged: Subject<string> = new Subject<string>();
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;
  selectedId: any;

  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getLocationsBasedOnData(this.currentPage);
  }

  getLocationsBasedOnData(pageIndex) {
    const filterData = {
      locationId: this.selectedId,
      searchString: this.searchString
    };
    this.locationService.getLocationByData(pageIndex, filterData).subscribe(res => {
      if (res) {
        this.manipulateResponse(res);
      }
    });
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAdd = res.featureJSON['canAdd'];
        this.isEdit = res.featureJSON['canEdit'];
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  manipulateResponse(res) {
    if (res && res['list']) {
      this.locationArr = [];
      this.locationArr = res['list'];
      this.filteredLocations = this.locationArr;
      this.isAPIBusy = false;
      this.totalCount = res['totalCount'];
    }
  }

  onIndexChange(event) {
    this.currentPage = event;
    this.getLocationsBasedOnData(event - 1);
  }

  resetCurrentPage() {
    this.currentPage = 0;
  }

  addLocation() {
    this.router.navigate([`addLocation`]);
  }

  editLocation(index) {
    this.router.navigate([`editLocation/${this.filteredLocations[index]['id']}`]);
  }

  deleteLocation(index) {
    this.locationService.deleteLocationById(this.filteredLocations[index]['id']).subscribe(res => {
      this.toasterService.showToast('success', 'Location Deleted successfully!', 'EMCARE');
      this.resetCurrentPage();
      this.getLocationsBasedOnData(this.currentPage);
    }, (err) => {
      alert(err.error);
    });
  }

  searchFilter() {
    this.resetCurrentPage();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        this.getLocationsBasedOnData(this.currentPage);
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  resetPageIndex() {
    this.currentPage = 0;
  }

  getLocationId(data) {
    this.selectedId = data;
    if (this.selectedId) {
      this.resetPageIndex();
      this.getLocationsBasedOnData(this.currentPage);
    } else {
      this.toasterService.showToast('info', 'Please select Location!', 'EMCARE')
    }
  }

  clearFilter(event) {
    if (event) {
      this.resetPageIndex();
      this.selectedId = null; 
      this.getLocationsBasedOnData(this.currentPage);
    }
  }
}
