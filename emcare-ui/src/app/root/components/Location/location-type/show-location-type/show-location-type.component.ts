import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { LocationService } from 'src/app/root/services/location.service';

@Component({
  selector: 'app-show-location-type',
  templateUrl: './show-location-type.component.html',
  styleUrls: ['./show-location-type.component.scss']
})
export class ShowLocationTypeComponent implements OnInit {

  locationTypeArr: any;
  filteredLocationTypes: any;
  searchString: string;

  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationTypes();
  }

  getLocationTypes() {
    this.locationTypeArr = [];
    this.locationService.getAllLocationTypes().subscribe(res => {
      if (res) {
        this.locationTypeArr = res;
        this.filteredLocationTypes = this.locationTypeArr;
      }
    });
  }

  addLocationType() {
    this.router.navigate([`addLocationType`]);
  }

  editLocationType(index) {
    this.router.navigate([`editLocationType/${this.filteredLocationTypes[index]['hierarchyType']}`]);
  }

  deleteLocationType(index) {
    this.locationService.deleteLocationTypeById(this.filteredLocationTypes[index]['hierarchyType']).subscribe(res => {
      this.toastr.success('Location Deleted successfully!!', 'EMCARE');
      this.getLocationTypes();
    });
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredLocationTypes = this.locationTypeArr.filter(locationType => {
      return (locationType.name?.toLowerCase().includes(lowerCasedSearchString)
        || locationType.code?.toLowerCase().includes(lowerCasedSearchString));
    });
  }
}
