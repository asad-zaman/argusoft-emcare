import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-location-management',
  templateUrl: './location-management.component.html',
  styleUrls: ['./location-management.component.scss']
})
export class LocationManagementComponent implements OnInit {

  locationForm: FormGroup;
  isEdit: boolean;
  editId: string;
  locationArr = [];
  locationTypeArr = [];

  constructor(
    private readonly formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationTypes();
    this.initLocationInputForm();
    this.setLocationArr();
    this.getAllLocations();
    this.checkEditParam();
  }

  getLocationTypes() {
    this.locationTypeArr = JSON.parse(localStorage.getItem('locationType'));
  }

  setLocationArr() {
    const data = localStorage.getItem('locations');
    if (data) {
      this.locationArr = JSON.parse(data);
    } else {
      this.locationArr = [];
    }
  }

  getAllLocations() {
    // this.nameArr = [];
    // this.fhirService.getAllOrganizations().subscribe(res => {
    //   if (res && res['entry']) {
    //     res['entry'].map(entry => {
    //       const fullName = `${entry['resource']['name']}`;
    //       this.nameArr.push({ name: fullName, id: entry['resource']['id'] });
    //     }, () => { });
    //   }
    // });
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.setCurrentLocation(this.editId);
      // this.fhirService.getOrganizationDetailById(this.editId).subscribe(res => {
      //   this.mapOrganizationData(res);
      // });
    }
  }

  setCurrentLocation(index) {
    const data = this.locationArr[index];
    this.locationForm.patchValue(data);
  }

  initLocationInputForm() {
    this.locationForm = this.formBuilder.group({
      locationType: ['', [Validators.required]],
      locationName: ['', [Validators.required]],
      parent: ['', []]
    });
  }

  saveData() {
    if (this.locationForm.valid) {
      const obj = this.locationForm.value;
      obj['id'] = this.locationArr.length > 0 ? this.locationArr.length : 0;
      if (this.isEdit) {
        this.locationArr[this.editId] = obj;
      } else {
        this.locationArr.push(obj);
      }
      localStorage.setItem('locations', JSON.stringify(this.locationArr));
      this.showLocation();
    }
  }

  showLocation() {
    this.router.navigate([`showLocation`]);
  }
}
