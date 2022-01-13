import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LocationService } from 'src/app/root/services/location.service';
import { ToasterService } from 'src/app/shared';
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

  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationsByPageIndex(this.currentPage);
  }

  getLocationsByPageIndex(index) {
    this.locationArr = [];
    this.locationService.getLocationsByPageIndex(index).subscribe(res => {
      if (res && res['list']) {
        this.locationArr = res['list'];
        this.filteredLocations = this.locationArr;
        this.isAPIBusy = false;
        this.totalCount = res['totalCount'];
      }
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
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredLocations = this.locationArr.filter(location => {
      return (location.name?.toLowerCase().includes(lowerCasedSearchString)
        || location.type?.toLowerCase().includes(lowerCasedSearchString)
        || location.parentName?.toLowerCase().includes(lowerCasedSearchString));
    });
  }
}
