import { Component, OnInit, Output, EventEmitter } from '@angular/core';
@Component({
  selector: 'app-location-filter',
  templateUrl: './location-filter.component.html',
  styleUrls: ['./location-filter.component.scss']
})
export class LocationFilterComponent implements OnInit {

  @Output() locationId = new EventEmitter<any>();
  sideMenu = false;
  currentUrl: string;
  currentLan;
  fornData;
  dropdownActiveArr = [];

  constructor() { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    const urlArr = location.href.split('/');
    this.currentUrl = urlArr[urlArr.length - 1];
    this.getAndSetCurrentLanguage();
  }

  getAndSetCurrentLanguage() {
    this.currentLan = localStorage.getItem('language');
  }

  saveData() {
    const valueArr = [
      this.fornData.country, this.fornData.state,
      this.fornData.city, this.fornData.region,
      this.fornData.other
    ];
    let selectedId;
    for (let index = this.dropdownActiveArr.length - 1; index >= 0; index--) {
      const data = this.dropdownActiveArr[index];
      //  if value is not selected and showing --select-- in dropdown then the parent valus should be emitted as selectedId
      if (data && (valueArr[index] !== "" && valueArr[index] !== "default") && !selectedId) {
        selectedId = valueArr[index];
      }
    }
    this.locationId.emit(selectedId);
    this.sideMenu = false;
  }

  getFormValue(event) {
    this.fornData = event.formData;
    this.dropdownActiveArr = event.dropdownArr;
  }
}
