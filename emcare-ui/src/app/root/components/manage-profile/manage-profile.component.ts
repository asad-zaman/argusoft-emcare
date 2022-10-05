import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService, FhirService, ToasterService } from 'src/app/shared';
import { UserManagementService } from '../../services/user-management.service';
import * as _ from 'lodash';
import { LaunguageSubjects } from 'src/app/auth/token-interceptor';
import { SearchCountryField, CountryISO, PhoneNumberFormat } from 'ngx-intl-tel-input';

@Component({
  selector: 'app-manage-profile',
  templateUrl: './manage-profile.component.html',
  styleUrls: ['./manage-profile.component.scss']
})
export class ManageProfileComponent implements OnInit {

  userId;
  currentUserForm: FormGroup;
  submitted: boolean;
  facilityIds;
  lanArr = [];

  separateDialCode = true;
  SearchCountryField = SearchCountryField;
  CountryISO = CountryISO;
  PhoneNumberFormat = PhoneNumberFormat;
  preferredCountries: CountryISO[] = [CountryISO.Iraq, CountryISO.UnitedStates];

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly userService: UserManagementService,
    private readonly formBuilder: FormBuilder,
    private readonly toasterService: ToasterService,
    private readonly router: Router,
    private readonly fhirService: FhirService,
    private readonly lanSubjects: LaunguageSubjects
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.initForm();
    this.getAllLaunguages();
  }

  getAllLaunguages() {
    this.fhirService.getAllLaunguagesTranslations().subscribe(res => {
      if (res) {
        _.forIn(res, (value, _key) => {
          this.lanArr.push({ id: value.languageCode, name: value.languageName, translations: value.languageData });
        });
        this.getLoggedInUserId();
      }
    });
  }

  initForm() {
    this.currentUserForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required]],
      language: ['', [Validators.required]],
      countryCode: [CountryISO.Iraq],
      phone: ['', [Validators.required]],  // 10 digit number
    });
  }

  getLaunguageObjFromCode(lan) {
    return this.lanArr.find(el => el.id === lan);
  }

  getLoggedInUserId() {
    this.authenticationService.getLoggedInUser().subscribe(res => {
      if (res) {
        this.userId = res['userId'];
        this.currentUserForm.patchValue({
          firstName: res['firstName'],
          lastName: res['lastName'],
          email: res['email'],
          countryCode: res['countryCode'],
          phone: res['phone'],
          language: this.getLaunguageObjFromCode(res['language'])
        });
        this.facilityIds = res['facilities'] && res['facilities'].map(f => f.facilityId);
      }
    });
  }

  get f() {
    return this.currentUserForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.currentUserForm.valid) {
      const data = {
        firstName: this.f.firstName.value,
        lastName: this.f.lastName.value,
        language: this.f.language.value.id,
        facilityIds: this.facilityIds,
        countryCode: this.f.phone.value.countryCode,
        phone: this.f.phone.value.number
      }
      const translations = this.lanArr.find(el => el.id === this.f.language.value.id).translations;
      this.userService.updateUser(data, this.userId).subscribe(() => {
        localStorage.setItem('language', this.f.language.value.id);
        this.lanSubjects.setLaunguage(this.f.language.value.id);
        this.lanSubjects.setCurrentTranslation(translations);
        this.router.navigate(['/dashboard']);
        this.toasterService.showToast('success', 'User profile updated successfully!', 'EMCARE');
      });
    }
  }
}
