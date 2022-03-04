import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { LocationService } from 'src/app/root/services/location.service';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-show-facility',
  templateUrl: './show-facility.component.html',
  styleUrls: ['./show-facility.component.scss']
})
export class ShowFacilityComponent implements OnInit {

  facilityArr: Array<any> = [];
  searchString;
  isAPIBusy: boolean = true;
  locationArr: Array<any> = [];
  searchTermChanged: Subject<string> = new Subject<string>();

  constructor(
    private readonly router: Router,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly locationService: LocationService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getFacilities();
    this.getAllLocations();
  }

  getLocationNameByID(id) {
    return this.locationArr.find(el => el.id == id).name;
  }

  capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  getAllLocations() {
    this.isAPIBusy = true;
    this.locationService.getAllLocations().subscribe((res: Array<Object>) => {
      this.isAPIBusy = false;
      if (res) {
        this.locationArr = res;
      }
    })
  }

  getFacilities() {
    this.fhirService.getFacility().subscribe(res => {
      this.manipulateRes(res);
    });
  }

  editFacility(index) {
    this.router.navigate([`manageFacility/${this.facilityArr[index]['id']}`]);
  }

  deleteFacility(index) {
    this.deleteOrganization(this.facilityArr[index]['managingOrganization']['id']);
    this.deleteLocation(this.facilityArr[index]['id']);
  }

  deleteLocation(id) {
    this.fhirService.deleteFacility(id).subscribe(_res => {
      this.facilityArr = [];
      this.getFacilities();
      this.toasterService.showSuccess('Facility deleted successfully!', 'EMCARE');
    }, (_error) => {
      this.toasterService.showError('Facility could not deleted successfully!', 'EMCARE');
    });
  }

  deleteOrganization(id) {
    this.fhirService.deleteOrganization(id).subscribe(_res => { }, (_error) => {
      this.toasterService.showError('Facility could not deleted successfully!', 'EMCARE');
    });
  }

  searchFilter() {
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {
          this.facilityArr = [];
          this.fhirService.getOrganizationByPageIndexAndSearch(0, this.searchString).subscribe(res => {
            this.manipulateRes(res);
          });
        } else {
          this.facilityArr = [];
          this.getFacilities();
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  manipulateRes(res) {
    if (res && res['entry']) {
      res['entry'].map(el => {
        this.facilityArr.push(el.resource);
      });
    }
  }
}
