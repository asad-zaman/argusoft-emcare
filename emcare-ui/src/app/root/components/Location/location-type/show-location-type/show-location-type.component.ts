import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-show-location-type',
  templateUrl: './show-location-type.component.html',
  styleUrls: ['./show-location-type.component.scss']
})
export class ShowLocationTypeComponent implements OnInit {

  locationTypeArr = [];

  constructor(
    private router: Router
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationTypes();
  }

  getLocationTypes() {
    const data = localStorage.getItem('locationType');
    if (data) {
      this.locationTypeArr = JSON.parse(data);
      console.log(this.locationTypeArr);
    } else {
      this.locationTypeArr = [];
    }
  }

  addLocationType() {
    this.router.navigate([`addLocationType`]);
  }

  editLocationType(index) {
    this.router.navigate([`editLocationType/${index}`]);
  }

  deleteLocationType(index) {
    this.locationTypeArr.splice(index, 1);
    localStorage.setItem('locationType', JSON.stringify(this.locationTypeArr));
  }
}
