import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-show-location',
  templateUrl: './show-location.component.html',
  styleUrls: ['./show-location.component.scss']
})
export class ShowLocationComponent implements OnInit {

  locationArr = [];
  locationTypeArr: any;

  constructor(
    private router: Router
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationTypes();
    this.getLocations();
  }

  getLocationTypes() {
    const data = localStorage.getItem('locationType');
    if (data) {
      this.locationTypeArr = JSON.parse(data);
    }
  }

  getLocations() {
    const data = localStorage.getItem('locations');
    if (data) {
      this.locationArr = JSON.parse(data);
    } else {
      this.locationArr = [];
    }
  }

  getTypeName(index) {
    const data = this.locationTypeArr.find(el => el.level === index);
    return data.name;
  }

  addLocation() {
    this.router.navigate([`addLocation`]);
  }

  editLocation(index) {
    this.router.navigate([`editLocation/${index}`]);
  }

  deleteLocation(index) {
    this.locationArr.splice(index, 1);
    localStorage.setItem('locations', JSON.stringify(this.locationArr));
  }
}
