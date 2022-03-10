import { Component, OnInit, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { LocationService } from 'src/app/root/services/location.service';

@Component({
  selector: 'app-location-dropdown',
  templateUrl: './location-dropdown.component.html',
  styleUrls: ['./location-dropdown.component.scss']
})
export class LocationDropdownComponent implements OnInit {

  locationFilterForm: FormGroup;
  countryArr: Array<any> = [];
  stateArr = [];
  cityArr = [];
  regionArr = [];
  otherArr = [];
  dropdownActiveArr = [true, false, false, false, false];

  @Input() idArr?: Array<any>;
  @Output() locationFormValueAndDropdownArr = new EventEmitter<any>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly locationService: LocationService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  ngOnChanges(changes: SimpleChanges) {
    // only run when property "data" changed
    if (changes['idArr']) {
      this.insertDataFromIdArr(changes['idArr'].currentValue);
    }
  }

  prerequisite() {
    this.initLocationFilterForm();
    this.getAllLocations();
  }

  insertDataFromIdArr(arr) {
    if (arr.length > 0) {
      this.locationFilterForm.patchValue({ country: arr[0] });
      //  it means there are more ids apart from countries
      if (arr.length > 1) {
        arr.forEach((element, index) => {
          const event = { target: { value: element } };
          this.onClicked(event, index + 1);
          this.mapFormValue(index + 1, element)
        });
      }
    }
  }

  mapFormValue(index, id) {
    switch (index) {
      case 2:
        this.locationFilterForm.patchValue({ state: id });
        break;
      case 3:
        this.locationFilterForm.patchValue({ city: id });
        break;
      case 4:
        this.locationFilterForm.patchValue({ region: id });
        break;
      case 5:
        this.locationFilterForm.patchValue({ other: id });
        break;
    }
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
    });
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
    this.locationFormValueAndDropdownArr.emit({
      formData: this.locationFilterForm.value,
      dropdownArr: this.dropdownActiveArr
    });
  }
}
