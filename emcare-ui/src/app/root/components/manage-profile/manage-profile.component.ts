import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService, FhirService, ToasterService } from 'src/app/shared';
import { UserManagementService } from '../../services/user-management.service';
import * as _ from 'lodash';
import { LaunguageSubjects } from 'src/app/auth/token-interceptor';
@Component({
  selector: 'app-manage-profile',
  templateUrl: './manage-profile.component.html',
  styleUrls: ['./manage-profile.component.scss']
})
export class ManageProfileComponent implements OnInit {

  userId;
  currentUserForm: FormGroup;
  submitted: boolean;
  locationId;
  lanArr = [];

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
      language: ['', [Validators.required]]
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
          language: this.getLaunguageObjFromCode(res['language'])
        });
        this.locationId = res['location']['id'];
        this.getLoggedInUserData();
      }
    });
  }

  getLoggedInUserData() {
    this.userService.getUserById(this.userId).subscribe(data => {
      if (data) {
        this.currentUserForm.patchValue({
          firstName: data['firstName'],
          lastName: data['lastName']
        });
      }
    })
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
        locationId: this.locationId
      }
      const translations = this.lanArr.find(el => el.id === this.f.language.value.id).translations;
      this.userService.updateUser(data, this.userId).subscribe(() => {
        localStorage.setItem('language', this.f.language.value.id);
        this.lanSubjects.setLaunguage(this.f.language.value.id);
        this.lanSubjects.setCurrentTranslation(translations);
        this.router.navigate(['/dashboard']);
        this.toasterService.showSuccess('User profile updated successfully!', 'EMCARE');
      });
    }
  }
}
