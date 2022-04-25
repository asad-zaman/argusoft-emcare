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
  locationArr = [];

  @Input() isMultipleLocation?: boolean;
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
      this.locationFilterForm.patchValue({ country: this.getObjFromId(arr[0]) });
      //  it means there are more ids apart from countries
      if (arr.length > 1) {
        arr.forEach((element, index) => {
          this.mapFormValue(index + 1, element);
          if (this.isMultipleLocation) {
            if (Array.isArray(element)) {
              const idArr = [];
              element.forEach(id => {
                idArr.push({ id: id });
              });
              const event = { value: idArr };
              this.onClickedMultipleDropdown(event, index + 1);
            } else {
              const event = { value: [{ id: element }] };
              this.onClickedMultipleDropdown(event, index + 1);
            }
          } else {
            const event = { value: { id: element } };
            this.onClickedSingleDropdown(event, index + 1);
          }
        });
      }
    }
  }

  mapFormValue(index, id) {
    switch (index) {
      case 2:
        this.locationFilterForm.patchValue({ state: this.getObjFromId(id) });
        break;
      case 3:
        this.locationFilterForm.patchValue({ city: this.getObjFromId(id) });
        break;
      case 4:
        this.locationFilterForm.patchValue({ region: this.getObjFromId(id) });
        break;
      case 5:
        this.locationFilterForm.patchValue({ other: this.getObjFromId(id) });
        break;
    }
  }

  getObjFromId(id) {
    if (this.isMultipleLocation) {
      if (Array.isArray(id)) {
        const locationObjArr = [];
        id.forEach(id => {
          locationObjArr.push(this.locationArr.find(l => l.id === id));
        });
        return locationObjArr;
      } else {
        const data = this.locationArr.find(l => l.id === id);
        return [data];
      }
    } else {
      const data = this.locationArr.find(l => l.id === id);
      return data;
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
        this.locationArr = res;
        const data = res.find(el => el['parent'] === 0);
        this.getAllLocationsByType(data['type'], true);
      }
    })
  }

  getAllLocationsByType(type, isFirstDropdown) {
    // getting locations by type
    this.locationService.getAllLocationByType(type).subscribe((res: Array<Object>) => {
      if (isFirstDropdown) {
        if (!this.isMultipleLocation) {
          this.countryArr.push({ id: 'default', name: '-- Select --' });
        }
        this.countryArr = this.countryArr.concat(res);
      }
    });
  }

  getChildLocations(id, arr) {
    // getting child locations by id
    this.locationService.getChildLocationById(id).subscribe((res: Array<Object>) => {
      if (!this.isMultipleLocation) {
        arr.push({ id: 'default', name: '-- Select --' });
      }
      if (res) {
        res.forEach(element => {
          arr.push(element)
        });
      }
    });
  }

  checkIfNextDropdownVisible(valArr) {
    if (valArr.length === 1) {
      return true;
    } else {
      return false;
    }
  }

  proceedOnDropdownValue(dropdownNum) {
    switch (dropdownNum) {
      case 1:
        this.stateArr = [];
        this.locationFilterForm.patchValue({
          state: '',
          city: '',
          region: '',
          other: ''
        });
        break;
      case 2:
        this.dropdownActiveArr[2] = true;
        this.cityArr = [];
        this.locationFilterForm.patchValue({
          city: '',
          region: '',
          other: ''
        });
        break;
      case 3:
        this.regionArr = [];
        this.locationFilterForm.patchValue({
          region: '',
          other: ''
        });
        break;
      case 4:
        this.otherArr = [];
        this.locationFilterForm.patchValue({
          other: ''
        });
        break;
      default:
        break;
    }
  }

  onClickedMultipleDropdown(event, dropdownNum) {
    const result = this.checkIfNextDropdownVisible(event.value);
    if (result) {
      const val = event.value[0].id;
      if (dropdownNum == 1) {
        this.dropdownActiveArr = [true, true, false, false, false];
        this.proceedOnDropdownValue(dropdownNum);
        this.getChildLocations(val, this.stateArr);
      } else if (dropdownNum == 2) {
        this.dropdownActiveArr = [true, true, true, false, false];
        this.proceedOnDropdownValue(dropdownNum);
        this.getChildLocations(val, this.cityArr);
      } else if (dropdownNum == 3) {
        this.dropdownActiveArr = [true, true, true, true, false];
        this.proceedOnDropdownValue(dropdownNum);
        this.getChildLocations(val, this.regionArr);
      } else if (dropdownNum == 4) {
        this.dropdownActiveArr = [true, true, true, true, true];
        this.proceedOnDropdownValue(dropdownNum);
        this.getChildLocations(val, this.otherArr);
      }
    } else {
      if (dropdownNum == 1) {
        this.dropdownActiveArr = [true, false, false, false, false];
        this.proceedOnDropdownValue(dropdownNum);
      } else if (dropdownNum == 2) {
        this.dropdownActiveArr = [true, true, false, false, false];
        this.proceedOnDropdownValue(dropdownNum);
      } else if (dropdownNum == 3) {
        this.dropdownActiveArr = [true, true, true, false, false];
        this.proceedOnDropdownValue(dropdownNum);
      } else if (dropdownNum == 4) {
        this.dropdownActiveArr = [true, true, true, true, false];
        this.proceedOnDropdownValue(dropdownNum);
      }
    }
    this.locationFormValueAndDropdownArr.emit({
      formData: this.getIdFromFormValueForMultipleDropdown(this.locationFilterForm.value),
      dropdownArr: this.dropdownActiveArr
    });
  }

  getIdFromFormValueForMultipleDropdown(formValue) {
    return {
      country: formValue.country ? formValue.country = formValue.country.map(e => e.id) : '',
      state: formValue.state ? formValue.state = formValue.state.map(e => e.id) : '',
      city: formValue.city ? formValue.city = formValue.city.map(e => e.id) : '',
      region: formValue.region ? formValue.region = formValue.region.map(e => e.id) : '',
      other: formValue.other ? formValue.other = formValue.other.map(e => e.id) : ''
    }
  }

  onClickedSingleDropdown(event, dropdownNum) {
    const val = event.value.id;
    // getting child locations based on dropdown
    if (dropdownNum == 1 && val !== 'default') {
      this.dropdownActiveArr = [true, true, false, false, false];
      this.stateArr = [];
      this.locationFilterForm.patchValue({
        state: '',
        city: '',
        region: '',
        other: ''
      });
      this.getChildLocations(val, this.stateArr);
    } else if (dropdownNum == 2 && val !== 'default') {
      this.dropdownActiveArr = [true, true, true, false, false];
      this.cityArr = [];
      this.locationFilterForm.patchValue({
        city: '',
        region: '',
        other: ''
      });
      this.getChildLocations(val, this.cityArr);
    } else if (dropdownNum == 3 && val !== 'default') {
      this.dropdownActiveArr = [true, true, true, true, false];
      this.regionArr = [];
      this.locationFilterForm.patchValue({
        region: '',
        other: ''
      });
      this.getChildLocations(val, this.regionArr);
    } else if (dropdownNum == 4 && val !== 'default') {
      this.dropdownActiveArr = [true, true, true, true, true];
      this.otherArr = [];
      this.locationFilterForm.patchValue({
        other: ''
      });
      this.getChildLocations(val, this.otherArr);
    }
    // to remove dropdowns if value is reset
    if (val === 'default') {
      for (let index = dropdownNum; index < this.dropdownActiveArr.length; index++) {
        this.dropdownActiveArr[index] = false;
      }
    }
    this.locationFormValueAndDropdownArr.emit({
      formData: this.getIdFromFormValueForSingleDropdown(this.locationFilterForm.value),
      dropdownArr: this.dropdownActiveArr
    });
  }

  getIdFromFormValueForSingleDropdown(formValue) {
    return {
      country: formValue.country ? formValue.country.id : '',
      state: formValue.state ? formValue.state.id : '',
      city: formValue.city ? formValue.city.id : '',
      region: formValue.region ? formValue.region.id : '',
      other: formValue.other ? formValue.other.id : ''
    }
  }
}
