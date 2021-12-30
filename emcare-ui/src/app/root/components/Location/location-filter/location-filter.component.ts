import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { LocationService } from 'src/app/root/services/location.service';

@Component({
  selector: 'app-location-filter',
  templateUrl: './location-filter.component.html',
  styleUrls: ['./location-filter.component.scss']
})
export class LocationFilterComponent implements OnInit {

  locationFilterForm: FormGroup;
  countryArr: Array<any> = [];
  stateArr = [];
  cityArr = [];
  regionArr = [];
  otherArr = [];
  dropdownActiveArr = [true, false, false, false, false];
  @Output() locationId = new EventEmitter<any>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly locationService: LocationService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.initLocationFilterForm();
    this.getAllLocations();
  }

  getAllLocations() {
    this.locationService.getAllLocations().subscribe((res: Array<Object>) => {
      if (res) {
        const data = res.find(el => el['parent'] === 0);
        this.getAllLocationsByType(data['type'], true);
      }
    })
  }

  getAllLocationsByType(type, isFirstDropdown) {
    // getting locations by type
    this.locationService.getAllLocationByType(type).subscribe((res: Array<Object>) => {
      if (isFirstDropdown) {
        this.countryArr.push({ id: 'default', name: '-- Select --' });
        this.countryArr = this.countryArr.concat(res);
      }
    });
  }

  getChildLocations(id, arr) {
    // getting child locations by id
    this.locationService.getChildLocationById(id).subscribe((res: Array<Object>) => {
      arr.push({ id: 'default', name: '-- Select --' });
      if (res) {
        res.forEach(element => {
          arr.push(element)
        });
      }
    })
  }

  initLocationFilterForm() {
    this.locationFilterForm = this.formBuilder.group({
      country: [''],
      state: [''],
      city: [''],
      region: [''],
      other: ['']
    });
  }

  saveData() {
    const valueArr = [
      this.locationFilterForm.value.country, this.locationFilterForm.value.state,
      this.locationFilterForm.value.city, this.locationFilterForm.value.region,
      this.locationFilterForm.value.other
    ];
    let selectedId;
    for (let index = this.dropdownActiveArr.length - 1; index >= 0; index--) {
      const data = this.dropdownActiveArr[index];
      if (data && valueArr[index] !== "" && !selectedId) {
        selectedId = valueArr[index];
      }
    }
    console.log(selectedId);
    this.locationId.emit(selectedId);
  }

  onClicked(event, dropdownNum) {
    // getting child locations based on dropdown
    if (dropdownNum == 1 && event.target.value !== 'default') {
      this.dropdownActiveArr = [true, true, false, false, false];
      this.stateArr = [];
      this.locationFilterForm.patchValue({
        state: '',
        city: '',
        region: '',
        other: ''
      });
      this.getChildLocations(event.target.value, this.stateArr);
    } else if (dropdownNum == 2 && event.target.value !== 'default') {
      this.dropdownActiveArr[2] = true;
      this.cityArr = [];
      this.locationFilterForm.patchValue({
        city: '',
        region: '',
        other: ''
      });
      this.getChildLocations(event.target.value, this.cityArr);
    } else if (dropdownNum == 3 && event.target.value !== 'default') {
      this.dropdownActiveArr[3] = true;
      this.regionArr = [];
      this.locationFilterForm.patchValue({
        region: '',
        other: ''
      });
      this.getChildLocations(event.target.value, this.regionArr);
    } else if (dropdownNum == 4 && event.target.value !== 'default') {
      this.dropdownActiveArr[4] = true;
      this.otherArr = [];
      this.locationFilterForm.patchValue({
        other: ''
      });
      this.getChildLocations(event.target.value, this.otherArr);
    }
    // to remove dropdowns if value is reset
    if (event.target.value === 'default') {
      for (let index = dropdownNum; index < this.dropdownActiveArr.length; index++) {
        this.dropdownActiveArr[index] = false;
      }
    }
  }
}
