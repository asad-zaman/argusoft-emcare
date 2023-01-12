import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';
import { LocationService } from '../../../services/location.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-manage-facility',
  templateUrl: './manage-facility.component.html',
  styleUrls: ['./manage-facility.component.scss']
})
export class ManageFacilityComponent implements OnInit {

  facilityForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  submitted: boolean = false;
  locationArr: Array<any> = [];
  statusArr: Array<any> = [];
  selectedId: any;
  dropdownActiveArr = [];
  organizationId;
  formData;
  locationIdArr: Array<any> = [];
  isAddFeature: boolean = true;
  isEditFeature: boolean = true;
  isAllowed: boolean = true;
  orgArr = [];
  isOrganizationAsFacility = false;
  apiBusy: boolean = true;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly locationService: LocationService,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authGuard: AuthGuard
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
    forkJoin([
      this.fhirService.getAllOrganizations(),
      this.locationService.getAllLocations()
    ]).subscribe(result => {
      if (result && result.length > 0) {
        this.setAllOrganizations(result[0]);
        this.setAllLocations(result[1]);
        this.checkEditParam();
        this.apiBusy = false;
        //  toDo when both api succeds then only this should be init
        //  check and set values
        this.initFacilityForm();
      }
    }, (_e) => {
      this.toasterService.showToast('error', 'Server issue!', 'EM CARE !!');
    });
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAddFeature = res.featureJSON['canAdd'];
        this.isEditFeature = res.featureJSON['canEdit'];
        if (this.isAddFeature && this.isEditFeature) {
          this.isAllowed = true;
        } else if (this.isAddFeature && !this.isEdit) {
          this.isAllowed = true;
        } else if (!this.isEditFeature && this.isEdit) {
          this.isAllowed = false;
        } else if (!this.isAddFeature && this.isEdit) {
          this.isAllowed = true;
        } else if (this.isEditFeature && this.isEdit) {
          this.isAllowed = true;
        } else {
          this.isAllowed = false;
        }
      }
    });
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.fhirService.getFacilityById(this.editId).subscribe(res => {
        if (res) {
          const locationId = res['extension'][0].valueInteger;
          if (locationId) {
            this.locationService.getParentLocationsById(res['extension'][0].valueInteger).subscribe((res: Array<any>) => {
              this.locationIdArr = res.map(el => el.id).reverse();
            });
          }
          this.organizationId = res['managingOrganization']['id'];
          this.mapValuesToFacilityForm(res);
        }
      });
    }
    this.checkFeatures();
  }

  setAllOrganizations(res) {
    if (res && res['entry']) {
      this.orgArr = res['entry'].map(el => {
        return { id: el.resource.id, name: el.resource.name }
      });
    }
  }

  getStatusObjById(status) {
    return this.statusArr.find(el => el.id === status);
  }

  getOrgObjfromId(id) {
    return this.orgArr.find(el => el.id === id);
  }

  mapValuesToFacilityForm(obj) {
    this.facilityForm.patchValue({
      name: obj.name,
      organization: this.getOrgObjfromId(obj.managingOrganization.id),
      status: this.getStatusObjById(obj.status),
      addressStreet: obj.address.line[0],
      telecom: obj.telecom ? obj.telecom[0].value : '',
      location: this.getLocationObjFromName((obj['extension'][0].valueInteger)),
      latitude: obj.position.latitude,
      longitude: obj.position.longitude
    });
  }

  getLocationObjFromName(id) {
    return this.locationArr.find(loc => {
      return loc.id == Number(id)
    });
  }

  setAllLocations(res) {
    if (res) {
      this.locationArr = res;
    }
  }

  initFacilityForm() {
    this.facilityForm = this.formBuilder.group({
      name: ['', []],
      organization: ['', [Validators.required]],
      addressStreet: ['', [Validators.required]],
      status: ['', [Validators.required]],
      telecom: ['', [Validators.required]],
      location: ['', [Validators.required]],
      latitude: ['', [Validators.required]],
      longitude: ['', [Validators.required]],
      isOrganizationAsFacility: [false]
    });
  }

  get f() {
    return this.facilityForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.facilityForm.valid) {
      if (this.isEdit) {
        const jsonObj = this.getData(this.facilityForm.value);
        jsonObj['id'] = this.editId;
        this.fhirService.editFacility(jsonObj, this.editId).subscribe(_res => {
          this.toasterService.showToast('success', 'Facility updated successfully!', 'EMCARE');
          this.showFacilities();
        }, (_error) => {
          this.toasterService.showToast('error', 'Facility could not be updated successfully!', 'EMCARE');
        });
      } else {
        const jsonObj = this.getData(this.facilityForm.value);
        this.fhirService.addFacility(jsonObj).subscribe(_res => {
          this.toasterService.showToast('success', 'Facility added successfully!', 'EMCARE');
          this.showFacilities();
        }, (_error) => {
          this.toasterService.showToast('error', 'Facility could not be added successfully!', 'EMCARE');
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
      "name": facilityObj.name ? facilityObj.name : this.getOrgObjfromId(facilityObj.organization.id).name,
      "address": {
        "use": "work",
        "line": [
          facilityObj.addressStreet
        ]
      },
      "status": facilityObj.status && facilityObj.status.id,
      "managingOrganization": {
        "id": facilityObj.organization.id
      },
      "position": {
        "longitude": facilityObj.longitude,
        "latitude": facilityObj.latitude
      },
      "extension": [{
        "url": null,
        "valueInteger": facilityObj.location.id
      }],
      "telecom": [
        {
          "system": "phone",
          "value": facilityObj.telecom,
          "use": "work"
        }
      ]
    }
  }

  saveLocationData() {
    const valueArr = [
      this.formData.country, this.formData.state,
      this.formData.city, this.formData.region,
      this.formData.other
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

  getFormValue(event) {
    this.formData = event.formData;
    this.dropdownActiveArr = event.dropdownArr
  }

  getLocationCoordinates() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(position => {
        this.facilityForm.patchValue({
          longitude: position['coords']['longitude'],
          latitude: position['coords']['latitude']
        });
      });
    }
    else {
      this.toasterService.showToast('info', 'Geolocation is not supported by this browser!!', 'EMCARE');
    }
  }

  setFacility(e) {
    if (e.checked[0] === 'true') {
      this.setOrgAsFacility();
    }
  }

  checkOrgAsFacility() {
    const isOrganizationAsFacility = this.facilityForm.get('isOrganizationAsFacility').value[0];
    if (isOrganizationAsFacility) {
      this.setOrgAsFacility();
    }
  }

  setOrgAsFacility() {
    const org = this.facilityForm.get('organization').value;
    if (org && org.name) {
      this.facilityForm.get('name').setValue(org.name);
    }
  }

  redirectToOrganizations() {
    this.router.navigateByUrl('/showOrganizations', {
      state: {
        returnRoute: this.editId ? `editFacility/${this.editId}` : '/addFacility'
      }
    });
  }
}
