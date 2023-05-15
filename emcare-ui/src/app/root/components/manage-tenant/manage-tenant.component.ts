import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CountryISO, PhoneNumberFormat, SearchCountryField } from 'ngx-intl-tel-input';
import { forkJoin, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';
import { RoleManagementService } from '../../services/role-management.service';
import * as _ from 'lodash';
import enTrans from '../../../../assets/i18n/en.json';

@Component({
  selector: 'app-manage-tenant',
  templateUrl: './manage-tenant.component.html',
  styleUrls: ['./manage-tenant.component.scss']
})
export class ManageTenantComponent implements OnInit {

  tenantForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  submitted = [false, false, false, false];
  isAddFeature = true;
  isEditFeature = true;
  isAllowed = true;
  isTenantIdRepeat = false;
  isDomainRepeat = false;
  domainTermChanged: Subject<string> = new Subject<string>();
  tenantIdTermChanged: Subject<string> = new Subject<string>();
  separateDialCode = true;
  SearchCountryField = SearchCountryField;
  CountryISO = CountryISO;
  PhoneNumberFormat = PhoneNumberFormat;
  roles: any = [];
  lanArray: Array<any> = [];
  availableLanguages = [];
  orgArr = [];
  statusArr = [
    { id: 'active', name: 'Active' },
    { id: 'inactive', name: 'Inactive' }
  ];
  emailTermChanged: Subject<string> = new Subject<string>();
  count = 1;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard,
    private readonly fhirService: FhirService,
    private readonly roleService: RoleManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getRoles();
    this.initTenantForm();

    forkJoin([
      this.fhirService.getAllLaunguagesTranslations(),
      this.fhirService.getAllLaunguages()
    ]).subscribe(result => {
      if (result && result.length > 0) {
        this.manipulateFirstResult(result[0]);
        this.manipulateSecondResult(result[1]);
      }
    }, () => {
      this.toasterService.showToast('error', 'API issue!', 'EMCARE');
    });
  }

  manipulateFirstResult(res) {
    if (res) {
      _.forIn(res, (value, _key) => {
        this.lanArray.push(value);
      });
    }
  }

  manipulateSecondResult(res) {
    if (res) {
      const availArr = _.differenceBy(res['languages'], this.lanArray, 'languageName');
      availArr.map(lan => {
        this.availableLanguages.push({
          id: lan.language,
          lanName: lan.languageName,
          name: `${lan.languageName} => ${lan.nativeLanguageName}`
        });
      });
    }
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
      tenantPassword: ['', [Validators.required]],
      domain: ['', [Validators.required]],
      dbName: ['', [Validators.required]],
      dbPort: ['', [Validators.required]],
      // Organization
      organizationName: ['', [Validators.required]],
      addressStreet: ['', [Validators.required]],
      countryCodeForOrg: [CountryISO.UnitedStates],
      telecom: ['', [Validators.required]],
      status: [this.statusArr[0], [Validators.required]],
      // Administrative User
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
      countryCode: [CountryISO.UnitedStates],
      phone: ['', [Validators.required]],  // 10 digit number
      password: ['', Validators.required],
      // Language
      newSelectedLanguage: ['', [Validators.required]]
    });
  }

  getRoles() {
    this.roleService.getAllRoles().subscribe(res => {
      if (res) {
        this.roles = res;
      }
    }, () => {
      this.toasterService.showToast('error', 'Server issue!', 'EMCARE');
    });
  }

  get getFormConfrols() {
    return this.tenantForm.controls;
  }

  saveData() {
    this.submitted = [true, true, true, true];
    if (this.tenantForm.valid &&
      !this.isDomainRepeat &&
      !this.isTenantIdRepeat) {
      const data = {
        tenantId: this.tenantForm.get('tenantId').value,
        url: this.tenantForm.get('url').value,
        username: this.tenantForm.get('username').value,
        password: this.tenantForm.get('tenantPassword').value,
        domain: this.tenantForm.get('domain').value,
        databaseName: this.tenantForm.get('dbName').value,
        databasePort: this.tenantForm.get('dbPort').value,
        hierarchy: {
          hierarchyType: 'Country',
          name: 'Country',
          code: 'country',
        },
        location: {
          name: this.tenantForm.get('tenantId').value,
          type: 'country',
          parent: 0
        },
        organization: JSON.stringify(this.getOrganizationData(this.tenantForm.value)),
        user: {
          firstName: this.tenantForm.get('firstName').value,
          lastName: this.tenantForm.get('lastName').value,
          email: this.tenantForm.get('email').value,
          username: this.tenantForm.get('email').value,
          password: this.tenantForm.get('password').value,
          roleName: `${this.tenantForm.get('tenantId').value.replace(/ /g, "-")}_Admin`,
          countryCode: this.tenantForm.get('phone').value.countryCode,
          phone: this.tenantForm.get('phone').value.number,
          regRequestFrom: 'web',
          language: 'en',
          facilityIds: null
        },
        language: {
          languageCode: this.tenantForm.get('newSelectedLanguage').value['id'],
          languageName: this.tenantForm.get('newSelectedLanguage').value['lanName']
        },
        defaultLanguage: JSON.stringify(enTrans)
      };
      this.fhirService.addTenant(data).subscribe(() => {
        this.toasterService.showToast('success', 'Tenant added successfully!', 'EM CARE!');
        this.router.navigate(['/tenantList']);
      }, (e) => {
        if (e && e.errorMessage) {
          this.toasterService.showToast('error', e.errorMessage, 'EM CARE!');
        }
      });
    } else {
      if (!this.tenantForm.get('newSelectedLanguage').valid) {
        const message = 'Please select value for Language!';
        this.toasterService.showToast('error', message, 'EM CARE!');
      }
    }
  }

  checkField(field) {
    if (field === 1) {
      if (this.tenantIdTermChanged.observers.length === 0) {
        this.tenantIdTermChanged.pipe(
          debounceTime(1000),
          distinctUntilChanged()
        ).subscribe(_term => {
          if (this.getFormConfrols.tenantId.valid) {
            this.fhirService.checkTenantField('tenantId', this.getFormConfrols.tenantId.value).subscribe((res) => {
              if (res && res['statusCode'] === 200) {
                this.isTenantIdRepeat = false;
              }
            }, (error) => {
              if (error['status'] === 400) {
                this.isTenantIdRepeat = true;
                this.toasterService.showToast('error', 'Field is already exists!', 'EMCARE!');
              }
            });
          } else { }
        });
      }
      this.tenantIdTermChanged.next(this.getFormConfrols.tenantId.value);
    } else if (field === 2) {
      if (this.domainTermChanged.observers.length === 0) {
        this.domainTermChanged.pipe(
          debounceTime(1000),
          distinctUntilChanged()
        ).subscribe(_term => {
          if (this.getFormConfrols.domain.valid) {
            this.fhirService.checkTenantField('domain', this.getFormConfrols.domain.value).subscribe(res => {
              if (res && res['statusCode'] === 200) {
                this.isDomainRepeat = false;
              }
            }, (error) => {
              if (error['status'] === 400) {
                this.isDomainRepeat = true;
                this.toasterService.showToast('error', 'Field is already exists!', 'EMCARE!');
              }
            });
          } else { }
        });
      }
      this.domainTermChanged.next(this.getFormConfrols.domain.value);
    }
  }

  setOrganizationName(event) {
    this.orgArr = [];
    if (event && event.target.value) {
      this.orgArr.push({ id: event.target.value.toLowerCase(), name: event.target.value });
    }
  }

  checkEmail() {
    if (this.emailTermChanged.observers.length === 0) {
      this.emailTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.getFormConfrols.email.valid) {
          this.fhirService.checkEmail(this.getFormConfrols.email.value).subscribe(res => {
            if (res['status'] === 400) {
              this.toasterService.showToast('error', 'Email is already exists!', 'EMCARE!');
              this.getFormConfrols.email.reset();
            }
          });
        } else { }
      });
    }
    this.emailTermChanged.next(this.getFormConfrols.email.value);
  }

  getOrganizationData(orgObj) {
    return {
      "resourceType": "Organization",
      "name": orgObj.organizationName,
      "alias": [
        orgObj.organizationName
      ],
      "telecom": [
        {
          "system": "phone",
          "value": `${orgObj.telecom.number}`
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
    };
  }

  onBackward() {
    this.count -= 1;
  }

  onForward() {
    if (this.count === 1) {
      this.submitted[0] = true;
      if (
        !this.tenantForm.get('tenantId').valid ||
        !this.tenantForm.get('url').valid ||
        !this.tenantForm.get('username').valid ||
        !this.tenantForm.get('tenantPassword').valid ||
        !this.tenantForm.get('domain').valid ||
        !this.tenantForm.get('dbName').valid ||
        !this.tenantForm.get('dbPort').valid
      ) {
        const message = 'Please enter required value!';
        this.toasterService.showToast('error', message, 'EM CARE!');
      } else if (
        this.isDomainRepeat ||
        this.isTenantIdRepeat
      ) {
        const message = 'Please enter different Country, Database URL or Domain!';
        this.toasterService.showToast('error', message, 'EM CARE!');
      } else {
        this.count += 1;
      }
    } else if (this.count === 2 &&
      (
        !this.tenantForm.get('organizationName').valid ||
        !this.tenantForm.get('addressStreet').valid ||
        !this.tenantForm.get('telecom').valid
      )) {
      this.submitted[1] = true;
      const message = 'Please enter required value!';
      this.toasterService.showToast('error', message, 'EM CARE!');
    } else if (this.count === 3 &&
      (
        !this.tenantForm.get('firstName').valid ||
        !this.tenantForm.get('lastName').valid ||
        !this.tenantForm.get('email').valid ||
        !this.tenantForm.get('countryCode').valid ||
        !this.tenantForm.get('phone').valid ||
        !this.tenantForm.get('password').valid
      )) {
      this.submitted[2] = true;
      const message = 'Please enter required value!';
      this.toasterService.showToast('error', message, 'EM CARE!');
    } else {
      this.count += 1;
    }
  }
}
