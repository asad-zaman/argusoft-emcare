import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { LocationService } from 'src/app/root/services/location.service';

@Component({
  selector: 'app-location-management',
  templateUrl: './location-management.component.html',
  styleUrls: ['./location-management.component.scss']
})
export class LocationManagementComponent implements OnInit {

  locationForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  locationArr: any;
  locationTypeArr: any;
  submitted: boolean;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationTypes();
    this.initLocationInputForm();
    this.getAllLocations();
    this.checkEditParam();
  }

  getLocationTypes() {
    this.locationService.getAllLocationTypes().subscribe(res => {
      if (res) {
        this.locationTypeArr = res;
      }
    });
  }

  getAllLocations() {
    this.locationArr = [];
    this.locationService.getAllLocations().subscribe(res => {
      if (res) {
        this.locationArr = res;
      }
    });
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.locationService.getLocationById(this.editId).subscribe(res => {
        if (res) {
          const data = {
            locationType: res['type'],
            locationName: res['name'],
            parent: res['parent']
          };
          this.locationForm.patchValue(data);
        }
      });
    }
  }

  initLocationInputForm() {
    this.locationForm = this.formBuilder.group({
      locationType: ['', [Validators.required]],
      locationName: ['', [Validators.required]],
      parent: ['', []]
    });
  }

  get f() {
    return this.locationForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.locationForm.valid) {
      if (this.isEdit) {
        const data = {
          "id": this.editId,
          "name": this.locationForm.get('locationName').value,
          "type": this.locationForm.get('locationType').value,
          "parent": this.locationForm.get('parent').value
        }
        this.locationService.updateLocationById(data).subscribe(res => {
          if (res) {
            this.toastr.success('Location updated successfully!!', 'EMCARE');
            this.showLocation();
          }
        });
      } else {
        const data = {
          "name": this.locationForm.get('locationName').value,
          "type": this.locationForm.get('locationType').value,
          "parent": this.locationForm.get('parent').value
        }
        this.locationService.createLocation(data).subscribe(res => {
          if (res) {
            this.toastr.success('Location added successfully!!', 'EMCARE');
            this.showLocation();
          }
        });
      }
    }
  }

  showLocation() {
    this.router.navigate([`showLocation`]);
  }
}
