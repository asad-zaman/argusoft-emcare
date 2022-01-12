import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LocationService } from 'src/app/root/services/location.service';

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

  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocations();
  }

  getLocations() {
    this.locationArr = [];
    this.locationService.getAllLocations().subscribe(res => {
      if (res) {
        this.locationArr = res;
        this.filteredLocations = this.locationArr;
        this.isAPIBusy = false;
      }
    });
  }

  addLocation() {
    this.router.navigate([`addLocation`]);
  }

  editLocation(index) {
    this.router.navigate([`editLocation/${this.filteredLocations[index]['id']}`]);
  }

  deleteLocation(index) {
    this.locationService.deleteLocationById(this.filteredLocations[index]['id']).subscribe(res => {
      this.getLocations();
    }, (err) => {
      alert(err.error);
    });
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredLocations = this.locationArr.filter( location => {
      return ( location.name?.toLowerCase().includes(lowerCasedSearchString) 
      ||  location.type?.toLowerCase().includes(lowerCasedSearchString)
      ||  location.parentName?.toLowerCase().includes(lowerCasedSearchString));
    });
  }

}
