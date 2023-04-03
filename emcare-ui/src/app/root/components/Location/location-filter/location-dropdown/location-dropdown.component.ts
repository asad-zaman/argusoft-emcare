import { Component, OnInit, Output, EventEmitter, Input, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { LocationService } from 'src/app/root/services/location.service';
import { FhirService } from 'src/app/shared';

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
  facilityArr = [];

  @Input() isMultiplePage?;
  @Input() idArr?: Array<any>;
  @Input() isOtherPage?: boolean;
  @Output() locationFormValueAndDropdownArr = new EventEmitter<any>();

  eventsSubscription: Subscription;
  @Input() events: Observable<boolean>;
  currentSelection: number;

  @Input() isClearFilter?: boolean;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly locationService: LocationService,
    private readonly fhirService: FhirService,
    private readonly cdr: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {
    this.prerequisite();
    if (this.isMultiplePage) {
      this.eventsSubscription = this.events.subscribe(() => this.resetData());
    }
  }

  ngAfterViewChecked() {
    this.cdr.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    // only run when property "data" changed
    if (changes['idArr']) {
      this.insertDataFromIdArr(changes['idArr'].currentValue);
    }
    if (changes['isClearFilter']) {
      if (changes['isClearFilter'].currentValue) {
        this.resetData();
        this.cdr.detectChanges();
      }
    }
  }

  prerequisite() {
    this.initLocationFilterForm();
    this.getAllLocations();
    this.getFacilities();
  }

  getFacilities() {
    this.fhirService.getFacility().subscribe((res: Array<any>) => {
      if (res) {
        res.forEach(element => {
          this.facilityArr.push({
            id: element.facilityId,
            name: element.facilityName,
            organizationName: element.organizationName
          });
        });
      }
    });
  }

  insertDataFromIdArr(arr) {
    if (arr.length > 0) {
      this.locationFilterForm.patchValue({ country: this.getObjFromId(arr[0]) });
      //  it means there are more ids apart from countries
      if (arr.length > 1) {
        arr.forEach((element, index) => {
          this.mapFormValue(index + 1, element);
          const event = { value: { id: element } };
          this.onClicked(event, index + 1);
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
    const data = this.locationArr.find(l => l.id === id);
    return data;
  }

  initLocationFilterForm() {
    this.locationFilterForm = this.formBuilder.group({
      facility: [''],
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
        const data = res.find(el => (el['parent'] === null) || (el['parent'] === 0));
        // getting conuntries
        this.countryArr = res.filter(el => el['type'] === data['type']);
      }
    })
  }

  getChildLocations(id, arr) {
    // getting child locations by id
    this.locationService.getChildLocationById(id).subscribe((res: Array<Object>) => {
      if (res) {
        res.forEach(element => {
          arr.push(element)
        });
      }
    });
  }

  onChangeFacility() {
    // 1 is for facility
    this.currentSelection = 1;
    this.checkFacilityAndLocationAndRemoveFirstSelection();
  }

  onClicked(event, dropdownNum) {
    // 2 is for location
    this.currentSelection = 2;
    const val = event.value ? event.value.id : null;
    // getting child locations based on dropdown
    if (dropdownNum == 1 && val !== null) {
      this.dropdownActiveArr = [true, true, false, false, false];
      this.stateArr = [];
      this.locationFilterForm.patchValue({
        state: '',
        city: '',
        region: '',
        other: ''
      });
      this.getChildLocations(val, this.stateArr);
    } else if (dropdownNum == 2 && val !== null) {
      this.dropdownActiveArr = [true, true, true, false, false];
      this.cityArr = [];
      this.locationFilterForm.patchValue({
        city: '',
        region: '',
        other: ''
      });
      this.getChildLocations(val, this.cityArr);
    } else if (dropdownNum == 3 && val !== null) {
      this.dropdownActiveArr = [true, true, true, true, false];
      this.regionArr = [];
      this.locationFilterForm.patchValue({
        region: '',
        other: ''
      });
      this.getChildLocations(val, this.regionArr);
    } else if (dropdownNum == 4 && val !== null) {
      this.dropdownActiveArr = [true, true, true, true, true];
      this.otherArr = [];
      this.locationFilterForm.patchValue({
        other: ''
      });
      this.getChildLocations(val, this.otherArr);
    }
    // to remove dropdowns if value is reset
    if (val === null) {
      for (let index = dropdownNum; index < this.dropdownActiveArr.length; index++) {
        this.dropdownActiveArr[index] = false;
      }
    }
    this.checkFacilityAndLocationAndRemoveFirstSelection();
  }

  // if facility is selected then location should be removed
  // if location is selected then facility should be removed
  // as api is not ready yet so both things can not work together
  checkFacilityAndLocationAndRemoveFirstSelection() {
    if (this.currentSelection === 1) {
      this.dropdownActiveArr = [true, false, false, false, false];
      this.locationFilterForm.patchValue({
        country: null,
        state: null,
        city: null,
        region: null,
        other: null
      });
    } else {
      this.locationFilterForm.patchValue({
        facility: null
      });
    }
    this.emitData();
  }

  emitData() {
    this.locationFormValueAndDropdownArr.emit({
      formData: this.getIdFromFormValue(this.locationFilterForm.value),
      dropdownArr: this.dropdownActiveArr
    });
  }

  resetData() {
    this.locationFilterForm.reset();
    this.dropdownActiveArr = [true, false, false, false, false];
  }

  getIdFromFormValue(formValue) {
    return {
      facility: formValue.facility ? formValue.facility : '',
      country: formValue.country ? formValue.country.id : '',
      state: formValue.state ? formValue.state.id : '',
      city: formValue.city ? formValue.city.id : '',
      region: formValue.region ? formValue.region.id : '',
      other: formValue.other ? formValue.other.id : ''
    }
  }

  ngOnDestroy() {
    if (this.isMultiplePage) {
      this.eventsSubscription.unsubscribe();
    }
  }
}
