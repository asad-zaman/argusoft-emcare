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
    this.getLocationsByPageIndex(this.currentPage);
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
      this.locationArr = res['list'];
      this.filteredLocations = this.locationArr;
      this.isAPIBusy = false;
      this.totalCount = res['totalCount'];
    }
  }

  getLocationsByPageIndex(index) {
    this.locationArr = [];
    this.locationService.getLocationsByPageIndex(index).subscribe(res => {
      this.manipulateResponse(res);
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    this.getLocationsByPageIndex(event - 1);
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
      this.toasterService.showSuccess('Location Deleted successfully!', 'EMCARE');
      this.resetCurrentPage();
      this.getLocationsByPageIndex(this.currentPage);
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
        if (this.searchString && this.searchString.length >= 1) {
          this.locationArr = [];
          this.locationService.getLocationsByPageIndex(this.currentPage, this.searchString).subscribe(res => {
            this.manipulateResponse(res);
          });
        } else {
          this.getLocationsByPageIndex(this.currentPage);
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }
}
