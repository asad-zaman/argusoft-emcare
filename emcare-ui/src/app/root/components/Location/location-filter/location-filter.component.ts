import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { LocationSubjects } from './LocationSubject';
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
  formData;
  dropdownActiveArr = [];
  @Input() isFacilityNotAllowed?: boolean;
  @Input() isPatientPage?: boolean;
  @Output() isClear = new EventEmitter<any>();

  constructor(
    private readonly locSubjects: LocationSubjects
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    const urlArr = location.href.split('/');
    this.currentUrl = urlArr[urlArr.length - 1];
    this.getAndSetCurrentLanguage();
    this.locSubjects.setClearLocation(false);
  }

  getAndSetCurrentLanguage() {
    this.currentLan = localStorage.getItem('language');
  }

  saveData() {
    const valueArr = [
      this.formData.facility.id,
      this.formData.country, this.formData.state,
      this.formData.city, this.formData.region,
      this.formData.other
    ];
    let selectedId;
    for (let index = this.dropdownActiveArr.length - 1; index >= 0; index--) {
      const data = this.dropdownActiveArr[index];
      //  if value is not selected and showing --select-- in dropdown then the parent valus should be emitted as selectedId
      if (data && (valueArr[index] !== "") && !selectedId) {
        selectedId = valueArr[index];
      }
    }
    this.locationId.emit(selectedId);
    this.locSubjects.setClearLocation(false);
    this.sideMenu = false;
  }

  getFormValue(event) {
    this.formData = event.formData;
    this.dropdownActiveArr = event.dropdownArr;
  }

  clearFilter() {
    this.isClear.emit(true);
    this.locSubjects.setClearLocation(true);
    this.sideMenu = false;
  }
}
