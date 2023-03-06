import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-manage-tenant',
  templateUrl: './manage-tenant.component.html',
  styleUrls: ['./manage-tenant.component.scss']
})
export class ManageTenantComponent implements OnInit {

  tenantForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  submitted: boolean;
  isAddFeature = true;
  isEditFeature = true;
  isAllowed = true;
  isTenantIdRepeat = false;
  isURLRepeat = false;
  isDomainRepeat = false;
  domainTermChanged: Subject<string> = new Subject<string>();
  urlTermChanged: Subject<string> = new Subject<string>();
  tenantIdTermChanged: Subject<string> = new Subject<string>();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard,
    private readonly fhirService: FhirService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.initTenantForm();
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

  initTenantForm() {
    this.tenantForm = this.formBuilder.group({
      tenantId: ['', [Validators.required]],
      url: ['', [Validators.required]],
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
      domain: ['', [Validators.required]]
    });
  }

  get f() {
    return this.tenantForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.tenantForm.valid && !this.isDomainRepeat && !this.isTenantIdRepeat && !this.isURLRepeat) {
      const data = {
        tenantId: this.tenantForm.get('tenantId').value,
        url: this.tenantForm.get('url').value,
        username: this.tenantForm.get('username').value,
        password: this.tenantForm.get('password').value,
        domain: this.tenantForm.get('domain').value
      };
      this.fhirService.addTenant(data).subscribe(() => {
        this.toasterService.showToast('success', 'Tenant added successfully!!', 'EM CARE!');
        this.router.navigate(['/tenantList']);
      }, () => {
        this.toasterService.showToast('error', 'API issue!!', 'EM CARE!');
      });
    }
  }

  checkField(field) {
    if (field === 1) {
      if (this.tenantIdTermChanged.observers.length === 0) {
        this.tenantIdTermChanged.pipe(
          debounceTime(1000),
          distinctUntilChanged()
        ).subscribe(_term => {
          if (this.f.tenantId.valid) {
            this.fhirService.checkTenantField('tenantId', this.f.tenantId.value).subscribe(() => {
            }, (error) => {
              if (error['status'] === 400) {
                this.isTenantIdRepeat = true;
                this.toasterService.showToast('error', 'Field is already exists!!', 'EMCARE!');
              }
            });
          } else { }
        });
      }
      this.tenantIdTermChanged.next(this.f.tenantId.value);
    } else if (field === 2) {
      if (this.urlTermChanged.observers.length === 0) {
        this.urlTermChanged.pipe(
          debounceTime(1000),
          distinctUntilChanged()
        ).subscribe(_term => {
          if (this.f.url.valid) {
            this.fhirService.checkTenantField('url', this.f.url.value).subscribe(() => {
            }, (error) => {
              if (error['status'] === 400) {
                this.isURLRepeat = true;
                this.toasterService.showToast('error', 'Field is already exists!!', 'EMCARE!');
              }
            });
          } else { }
        });
      }
      this.urlTermChanged.next(this.f.url.value);
    } else if (field === 3) {
      if (this.domainTermChanged.observers.length === 0) {
        this.domainTermChanged.pipe(
          debounceTime(1000),
          distinctUntilChanged()
        ).subscribe(_term => {
          if (this.f.domain.valid) {
            this.fhirService.checkTenantField('domain', this.f.domain.value).subscribe(res => {
            }, (error) => {
              if (error['status'] === 400) {
                this.isDomainRepeat = true;
                this.toasterService.showToast('error', 'Field is already exists!!', 'EMCARE!');
              }
            });
          } else { }
        });
      }
      this.domainTermChanged.next(this.f.domain.value);
    }
  }

}
