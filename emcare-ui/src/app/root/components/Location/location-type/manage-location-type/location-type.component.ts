import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { LocationService } from 'src/app/root/services/location.service';
import { ToasterService } from 'src/app/shared';
@Component({
  selector: 'app-location-type',
  templateUrl: './location-type.component.html',
  styleUrls: ['./location-type.component.scss']
})
export class LocationTypeComponent implements OnInit {

  locationTypeForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  submitted: boolean;
  isAddFeature = true;
  isEditFeature = true;
  isAllowed = true;

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
    this.checkFeatures();
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    this.checkEditParam();
    this.initLocationTypeInputForm();
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
    if (this.editId) {
      this.isEdit = true;
      this.locationService.getLocationTypeById(this.editId).subscribe(res => {
        if (res) {
          const obj = {
            type: res['code'],
            name: res['name']
          }
          this.locationTypeForm.setValue(obj);
        }
      });
    }
  }

  initLocationTypeInputForm() {
    this.locationTypeForm = this.formBuilder.group({
      type: ['', [Validators.required]],
      name: ['', [Validators.required]]
    });
  }

  get f() {
    return this.locationTypeForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.locationTypeForm.valid) {
      if (this.isEdit) {
        const data = {
          "hierarchyType": this.editId,
          "name": this.locationTypeForm.get('name').value,
          "code": this.locationTypeForm.get('type').value
        };
        this.locationService.updateLocationTypeById(data).subscribe(() => {
          this.toasterService.showToast('success', 'Location Type updated successfully!', 'EM CARE');
          this.showLocationType();
        });
      } else {
        const data = {
          "hierarchyType": this.locationTypeForm.get('name').value,
          "name": this.locationTypeForm.get('name').value,
          "code": this.locationTypeForm.get('type').value
        };
        this.locationService.createLocationType(data).subscribe((res) => {
          this.toasterService.showToast('success', 'Location Type added successfully!', 'EM CARE');
          this.showLocationType();
        });
      }
    }
  }

  showLocationType() {
    this.router.navigate([`showLocationType`]);
  }
}
