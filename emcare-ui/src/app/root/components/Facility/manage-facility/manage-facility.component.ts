import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FhirService, ToasterService } from 'src/app/shared';
import { LocationService } from '../../../services/location.service';

@Component({
  selector: 'app-manage-facility',
  templateUrl: './manage-facility.component.html',
  styleUrls: ['./manage-facility.component.scss']
})
export class ManageFacilityComponent implements OnInit {

  facilityForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  submitted: boolean;
  locationArr: Array<any> = [];
  statusArr: Array<any> = [];
  selectedId: any;
  locationFilterForm: FormGroup;
  countryArr: Array<any> = [];
  stateArr = [];
  cityArr = [];
  regionArr = [];
  otherArr = [];
  dropdownActiveArr = [true, false, false, false, false];
  organizationId;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly locationService: LocationService,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {
    this.statusArr = [
      { id: 'active', name: 'Active' },
      { id: 'suspended', name: 'Suspended' },
      { id: 'inactive', name: 'Inactive' }
    ];
  }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.initFacilityForm();
    this.initLocationFilterForm();
    this.getAllLocations();
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.fhirService.getFacilityById(this.editId).subscribe(res => {
        if (res) {
          this.organizationId = res['managingOrganization']['id'];
          this.fhirService.getOrganizationById(this.organizationId).subscribe(orgRes => {
            if (orgRes) {
              this.mapValuesToFacilityForm(res, orgRes);
            }
          });
        }
      });
    }
  }

  getStatusObjById(status) {
    return this.statusArr.find(el => el.id === status);
  }

  mapValuesToFacilityForm(obj, orgObj) {
    // to remove
    this.facilityForm.patchValue({
      organizationName: orgObj.name,
      status: this.getStatusObjById(obj.status),
      addressStreet: obj.address.line[0],
      telecom: orgObj.telecom[0].value,
      location: this.getLocationObjFromName((obj.extension[0].valueIdentifier.value))
    });
  }

  getLocationObjFromName(id) {
    return this.locationArr.find(loc => {
      return loc.id == Number(id)
    });
  }

  getAllLocations() {
    this.locationService.getAllLocations().subscribe((res: Array<Object>) => {
      if (res) {
        this.locationArr = res;
        const data = res.find(el => el['parent'] === 0);
        this.checkEditParam();
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

  initFacilityForm() {
    this.facilityForm = this.formBuilder.group({
      organizationName: ['', [Validators.required]],
      addressStreet: ['', [Validators.required]],
      status: ['', [Validators.required]],
      telecom: ['', [Validators.required]],
      location: ['', [Validators.required]]
    });
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

  get f() {
    return this.facilityForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.facilityForm.valid) {
      const organizationObj = this.getOrganizationJSON(this.facilityForm.value);
      if (this.isEdit) {
        organizationObj['id'] = this.organizationId;
        this.fhirService.updateOrganization(organizationObj, this.organizationId).subscribe(res => {
          if (res) {
            const jsonObj = this.getData(this.facilityForm.value);
            jsonObj['id'] = this.editId;
            this.fhirService.editFacility(jsonObj, this.editId).subscribe(_res => {
              this.toasterService.showSuccess('Facility updated successfully!', 'EMCARE');
              this.showFacilities();
            }, (_error) => {
              this.toasterService.showError('Facility could not be updated successfully!', 'EMCARE');
            });
          }
        }, (_error) => {
          this.toasterService.showError('Organization could not be updated successfully!', 'EMCARE');
        });
      } else {
        this.fhirService.addOrganization(organizationObj).subscribe(res => {
          if (res) {
            this.organizationId = res['id'];
            const jsonObj = this.getData(this.facilityForm.value);
            this.fhirService.addFacility(jsonObj).subscribe(_res => {
              this.toasterService.showSuccess('Facility added successfully!', 'EMCARE');
              this.showFacilities();
            }, (_error) => {
              this.toasterService.showError('Facility could not be added successfully!', 'EMCARE');
            });
          }
        }, (_error) => {
          this.toasterService.showError('Organization could not be added successfully!', 'EMCARE');
        });
      }
    }
  }

  showFacilities() {
    this.router.navigate([`showFacility`]);
  }

  getData(facilityObj) {
    return {
      "resourceType": "Location",
      "address": {
        "use": "work",
        "line": [
          facilityObj.addressStreet
        ]
      },
      "status": facilityObj.status && facilityObj.status.id,
      "managingOrganization": {
        "id": this.organizationId
      },
      "extension": [
        {
          "valueIdentifier": {
            "use": "official",
            "value": facilityObj.location.id
          }
        }
      ]
    }
  }

  getOrganizationJSON(facilityObj) {
    return {
      "resourceType": "Organization",
      "name": facilityObj.organizationName,
      "telecom": [
        {
          "system": "phone",
          "value": facilityObj.telecom,
          "use": "work"
        }
      ]
    }
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

  saveLocationData() {
    const valueArr = [
      this.locationFilterForm.value.country, this.locationFilterForm.value.state,
      this.locationFilterForm.value.city, this.locationFilterForm.value.region,
      this.locationFilterForm.value.other
    ];
    let selectedId;
    for (let index = this.dropdownActiveArr.length - 1; index >= 0; index--) {
      const data = this.dropdownActiveArr[index];
      //  if value is not selected and showing --select-- in dropdown then the parent valus should be emitted as selectedId
      if (data && (valueArr[index] !== "" && valueArr[index] !== "default") && !selectedId) {
        selectedId = valueArr[index];
      }
    }
    const selectedLocation = this.locationArr.find(el => el.id == selectedId);
    this.facilityForm.patchValue({
      location: selectedLocation
    });
  }
}
