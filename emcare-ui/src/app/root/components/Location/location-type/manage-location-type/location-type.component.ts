import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-location-type',
  templateUrl: './location-type.component.html',
  styleUrls: ['./location-type.component.scss']
})
export class LocationTypeComponent implements OnInit {

  locationTypeForm: FormGroup;
  isEdit: boolean;
  editId: string;
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
    this.initLocationTypeInputForm();
    this.setLocationTypeArr();
    this.getAllLocationTypes();
    this.checkEditParam();
  }

  setLocationTypeArr() {
    const data = localStorage.getItem('locationType');
    if (data) {
      this.locationTypeArr = JSON.parse(data);
    } else {
      this.locationTypeArr = [];
    }
  }

  getAllLocationTypes() {
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
      this.setCurrentLocationType(this.editId);
      // this.fhirService.getOrganizationDetailById(this.editId).subscribe(res => {
      //   this.mapOrganizationData(res);
      // });
    }
  }

  setCurrentLocationType(index) {
    const data = this.locationTypeArr[index];
    this.locationTypeForm.setValue(data);
  }

  initLocationTypeInputForm() {
    this.locationTypeForm = this.formBuilder.group({
      type: ['', [Validators.required]],
      name: ['', [Validators.required]],
      level: ['', [Validators.required]]
    });
  }

  saveData() {
    if (this.locationTypeForm.valid) {
      const obj = this.locationTypeForm.value;
      if (this.isEdit) {
        this.locationTypeArr[this.editId] = obj;
      } else {
        this.locationTypeArr.push(obj);
      }
      localStorage.setItem('locationType', JSON.stringify(this.locationTypeArr));
      this.showLocationType();
    }
  }

  showLocationType() {
    this.router.navigate([`showLocationType`]);
  }
}
