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
  countryArr: Array<any> = [];
  locationArr: Array<any> = [];
  statusArr: Array<any> = [];
  selectedId: any;

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
    this.getAllLocations();
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.fhirService.getFacilityById(this.editId).subscribe(res => {
        if (res) {
          this.mapValuesToFacilityForm(res);
        }
      });
    }
  }

  mapValuesToFacilityForm(obj) {
    // to remove
    this.facilityForm.patchValue({
      locationName: obj.name,
      // locationAlias: obj.alias[0],
      // locationDescription: obj.description,
      phone: obj.telecom[0].value,
      fax: obj.telecom[1].value,
      email: obj.telecom[2].value,
      // url: obj.telecom[3].value,
      addressStreet: obj.address.line[0],
      // city: obj.address.city,
      // postalCode: obj.address.postalCode,
      // country: obj.address.country,
      // longitude: obj.position.longitude,
      // latitude: obj.position.latitude,
      // altitude: obj.position.altitude,
      managingOrganization: this.getLocationObjFromName(obj.managingOrganization.identifier.id)
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
        const data = res.filter(el => el['parent'] === 0);
        this.countryArr = data;
        this.checkEditParam();
      }
    })
  }

  initFacilityForm() {
    this.facilityForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      // locationAlias: ['', [Validators.required]],
      // locationDescription: ['', [Validators.required]],
      // phone: ['', [Validators.required, Validators.pattern('^[0-9]*$')]],
      // fax: ['', [Validators.required]],
      // email: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
      // url: ['', [Validators.required]],
      addressStreet: ['', [Validators.required]],
      status: ['', [Validators.required]],
      // city: ['', [Validators.required]],
      // postalCode: ['', [Validators.required]],
      // country: ['', [Validators.required]],
      // longitude: ['', [Validators.required]],
      // latitude: ['', [Validators.required]],
      // altitude: ['', [Validators.required]],
      organizationName: ['', [Validators.required]],
      telecom: ['', [Validators.required]]
    });
  }

  get f() {
    return this.facilityForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.facilityForm.valid) {
      console.log(this.facilityForm.value);
      // const jsonObj = this.getData(this.facilityForm.value);
      // if (this.isEdit) {
      //   jsonObj['id'] = this.editId;
      //   this.fhirService.editFacility(jsonObj, this.editId).subscribe(_res => {
      //     this.toasterService.showSuccess('Facility updated successfully!', 'EMCARE');
      //     this.showFacilities();
      //   }, (_error) => {
      //     this.toasterService.showError('Facility could not be updated successfully!', 'EMCARE');
      //   });
      // } else {
      //   this.fhirService.addFacility(jsonObj).subscribe(_res => {
      //     this.toasterService.showSuccess('Facility added successfully!', 'EMCARE');
      //     this.showFacilities();
      //   }, (_error) => {
      //     this.toasterService.showError('Facility could not be added successfully!', 'EMCARE');
      //   });
      // }
    }
  }

  showFacilities() {
    this.router.navigate([`showFacility`]);
  }

  getData(facilityObj) {
    return {
      "resourceType": "Location",
      "name": facilityObj.locationName,
      "alias": [
        facilityObj.locationAlias
      ],
      "description": facilityObj.locationDescription,
      "mode": "instance",
      "telecom": [
        {
          "system": "phone",
          "value": facilityObj.phone,
          "use": "work"
        },
        {
          "system": "fax",
          "value": facilityObj.fax,
          "use": "work"
        },
        {
          "system": "email",
          "value": facilityObj.email
        },
        {
          "system": "url",
          "value": facilityObj.url,
          "use": "work"
        }
      ],
      "address": {
        "use": "work",
        "line": [
          facilityObj.addressStreet
        ],
        "city": facilityObj.city,
        "postalCode": facilityObj.postalCode,
        "country": facilityObj.country.name
      },
      "position": {
        "longitude": facilityObj.longitude,
        "latitude": facilityObj.latitude,
        "altitude": facilityObj.altitude
      },
      "managingOrganization": {
        "type": "Location", // Type the reference refers to (e.g. "Patient")
        "identifier": facilityObj.managingOrganization, // Logical reference, when literal reference is not known
        "display": facilityObj.managingOrganization.name // Text alternative for the resource
      }
    }
  }

  getLocationId(data) {
    this.selectedId = data;
    if (this.selectedId) {
      console.log(this.selectedId);
    }
  }
}
