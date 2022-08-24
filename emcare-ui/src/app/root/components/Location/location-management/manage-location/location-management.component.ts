import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { LocationService } from 'src/app/root/services/location.service';
import { ToasterService } from 'src/app/shared';
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
  isAddFeature: boolean = true;
  isEditFeature: boolean = true;
  isAllowed: boolean = true;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly locationService: LocationService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationTypes();
    this.initLocationInputForm();
    this.getAllLocations();
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
        this.checkEditParam();
      }
    });
  }

  findLocationType(type) {
    return this.locationTypeArr.find(t => t.code === type);
  }

  findLocation(locationId) {
    return this.locationArr.find(l => l.id === locationId);
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.locationService.getLocationById(this.editId).subscribe(res => {
        if (res) {
          const data = {
            locationType: this.findLocationType(res['type']),
            locationName: res['name'],
            parent: this.findLocation(res['parent'])
          };
          this.locationForm.patchValue(data);
        }
      });
    }
    this.checkFeatures();
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
          "type": this.locationForm.get('locationType').value ? this.locationForm.get('locationType').value.code : '',
          "parent": this.locationForm.get('parent').value ? this.locationForm.get('parent').value.id : ''
        }
        this.locationService.updateLocationById(data).subscribe(res => {
          if (res) {
            this.toasterService.showToast('success', 'Location updated successfully!', 'EMCARE');
            this.showLocation();
          }
        });
      } else {
        const data = {
          "name": this.locationForm.get('locationName').value,
          "type": this.locationForm.get('locationType').value ? this.locationForm.get('locationType').value.code : '',
          "parent": this.locationForm.get('parent').value ? this.locationForm.get('parent').value.id : ''
        }
        this.locationService.createLocation(data).subscribe(res => {
          if (res) {
            this.toasterService.showToast('success', 'Location added successfully!', 'EMCARE');
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
