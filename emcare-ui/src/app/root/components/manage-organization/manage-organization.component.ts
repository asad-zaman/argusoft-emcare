import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';
@Component({
  selector: 'app-manage-organization',
  templateUrl: './manage-organization.component.html',
  styleUrls: ['./manage-organization.component.scss']
})
export class ManageOrganizationComponent implements OnInit {

  orgForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  submitted: boolean;
  isAddFeature: boolean = true;
  isEditFeature: boolean = true;
  isAllowed: boolean = true;
  statusArr = [];

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authGuard: AuthGuard
  ) {
    this.statusArr = [
      { id: 'active', name: 'Active' },
      { id: 'inactive', name: 'Inactive' }
    ];
  }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.initOrgForm();
    this.checkEditParam();
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
      this.fhirService.getOrganizationById(this.editId).subscribe(res => {
        if (res) {
          this.mapValuesToOrgForm(res);
        }
      });
    }
    this.checkFeatures();
  }

  mapValuesToOrgForm(orgObj) {
    this.orgForm.patchValue({
      organizationName: orgObj.name,
      alias: orgObj.alias && orgObj.alias[0] ? orgObj.alias[0] : 'u',
      addressStreet: orgObj.address && orgObj.address[0].line[0],
      telecom: orgObj.telecom && orgObj.telecom[0].value,
      status: orgObj.active === true ? { id: 'active', name: 'Active' } : { id: 'inactive', name: 'Inactive' }
    });
  }

  initOrgForm() {
    this.orgForm = this.formBuilder.group({
      organizationName: ['', [Validators.required]],
      alias: ['', [Validators.required]],
      addressStreet: ['', [Validators.required]],
      telecom: ['', [Validators.required]],
      status: ['', [Validators.required]]
    });
  }

  get f() {
    return this.orgForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.orgForm.valid) {
      let organizationObj = this.getData(this.orgForm.value);
      if (this.isEdit) {
        organizationObj['id'] = this.editId;
        this.fhirService.updateOrganization(organizationObj, this.editId).subscribe(res => {
          if (res) {
            this.showOrganizations();
          }
        }, (_error) => {
          this.toasterService.showToast('error', 'Organization could not be updated successfully!', 'EMCARE');
        });
      } else {
        this.fhirService.addOrganization(organizationObj).subscribe(res => {
          if (res) {
            this.toasterService.showToast('success', 'Facility added successfully!', 'EMCARE');
            this.showOrganizations();
          }
        }, (_error) => {
          this.toasterService.showToast('error', 'Organization could not be added successfully!', 'EMCARE');
        });
      }
    }
  }

  showOrganizations() {
    this.router.navigate([`showOrganizations`]);
  }

  getData(orgObj) {
    return {
      "resourceType": "Organization",
      "name": orgObj.organizationName,
      "alias": [
        orgObj.alias
      ],
      "telecom": [
        {
          "system": "phone",
          "value": orgObj.telecom
        }
      ],
      "address": [
        {
          "line": [
            orgObj.addressStreet
          ]
        }
      ],
      "active": orgObj.status.id === 'active' ? true : false
    }
  }
}
