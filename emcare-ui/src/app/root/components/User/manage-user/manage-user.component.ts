import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { RoleManagementService } from 'src/app/root/services/role-management.service';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { FhirService, ToasterService } from 'src/app/shared';
import { MustMatch } from 'src/app/shared/validators/must-match.validator';
import { SearchCountryField, CountryISO, PhoneNumberFormat } from 'ngx-intl-tel-input';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.scss']
})
export class ManageUserComponent implements OnInit {

  userForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  roles: any = [];
  submitted = false;
  isAddFeature: boolean = true;
  isEditFeature: boolean = true;
  isAllowed: boolean = true;
  facilityArr = [];
  selectedFacility;

  separateDialCode = true;
  SearchCountryField = SearchCountryField;
  CountryISO = CountryISO;
  PhoneNumberFormat = PhoneNumberFormat;
  preferredCountries: CountryISO[] = [CountryISO.Iraq, CountryISO.UnitedStates];
  language: string;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly userService: UserManagementService,
    private readonly roleService: RoleManagementService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard,
    private readonly fhirService: FhirService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    this.checkEditParams();
    this.initUserForm();
    this.getRoles();
    if (this.isEdit) {
      this.mapUpdateForm();
    } else { }
    this.getFacilities();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAddFeature = res.featureJSON['canAdd'];
        this.isEditFeature = res.featureJSON['canEdit'];
        if (this.isEdit) {
          this.isAllowed = this.isEditFeature || !this.isAddFeature ? true : false;
        } else if (this.isAddFeature) {
          this.isAllowed = this.isEditFeature || !this.isEdit ? true : false;
        } else {
          this.isAllowed = false;
        }
      }
    });
  }

  checkEditParams() {
    if (this.editId) {
      this.isEdit = true;
    } else {
      this.isEdit = false;
    }
  }

  mapFacilityRes(facilityArr) {
    return facilityArr.map(el => {
      return { id: el.facilityId, name: el.facilityName, organizationName: el.organizationName }
    });
  }

  mapUpdateForm() {
    this.userService.getUserById(this.editId).subscribe(res => {
      if (res) {
        const data = {
          firstName: res['firstName'],
          lastName: res['lastName'],
          username: res['userName'],
          facility: this.mapFacilityRes(res['facilities']),
          countryCode: res['countryCode'],
          phone: res['phone'],
          role: this.roles.find(el => el['name'] === res['realmRoles'][0])
        };
        this.language = res['language'];
        this.userForm.patchValue(data);
      }
    });
  }

  initUserForm() {
    if (this.isEdit) {
      this.userForm = this.formBuilder.group({
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        countryCode: [CountryISO.Iraq],
        phone: ['', [Validators.required]],
        selectedFacility: [''],
        facility: ['', Validators.required],
        role: ['', [Validators.required]]
      });
      this.userForm.addControl('username', new FormControl({ value: '', disabled: true }, Validators.required));
    } else {
      this.userForm = this.formBuilder.group({
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
        countryCode: [CountryISO.Iraq],
        phone: ['', [Validators.required]],  // 10 digit number
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required],
        role: ['', [Validators.required]],
        selectedFacility: [''],
        facility: ['', Validators.required]
      }, {
        validator: MustMatch('password', 'confirmPassword')
      });
      this.userForm.addControl('username', new FormControl('', Validators.required));
    }
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
    return this.userForm.controls;
  }

  getFacilityIdFromArr(facilityArr) {
    return facilityArr.map(el => el.id);
  }

  saveData() {
    this.submitted = true;
    if (this.userForm.valid) {
      if (this.isEdit) {
        const data = {
          "firstName": this.userForm.get('firstName').value,
          "lastName": this.userForm.get('lastName').value,
          "regRequestFrom": "web",
          "facilityIds": this.getFacilityIdFromArr(this.userForm.get('facility').value),
          "countryCode": this.userForm.get('phone').value.countryCode,
          //  saving countries national number as code is already shown in input & dropdown tag
          "phone": this.userForm.get('phone').value.number,
          "language": this.language,
          "roleName": this.userForm.get('role').value ? this.userForm.get('role').value.name : '',
        }
        this.userService.updateUser(data, this.editId).subscribe(res => {
          this.toasterService.showToast('success', 'User updated successfully!', 'EMCARE');
          this.showUser();
        });
      } else {
        const data = {
          "firstName": this.userForm.get('firstName').value,
          "lastName": this.userForm.get('lastName').value,
          "email": this.userForm.get('email').value,
          "password": this.userForm.get('password').value,
          "roleName": this.userForm.get('role').value ? this.userForm.get('role').value.name : '',
          "regRequestFrom": "web",
          "userName": this.userForm.get('username').value,
          "facilityIds": this.getFacilityIdFromArr(this.userForm.get('facility').value),
          "countryCode": this.userForm.get('phone').value.countryCode,
          "phone": this.userForm.get('phone').value.number,
          "language": this.language
        }
        this.userService.createUser(data).subscribe(res => {
          this.toasterService.showToast('success', 'User added successfully!', 'EMCARE');
          this.showUser();
        });
      }
    }
  }

  showUser() {
    this.router.navigate([`showUsers`]);
  }

  getFacilities() {
    this.fhirService.getFacility().subscribe((res: Array<any>) => {
      if (res) {
        res.forEach(element => {
          this.facilityArr.push({
            id: element.facilityId,
            name: element.facilityName,
            organizationName: element.organizationName
          });
        });
      }
    });
  }

  saveFacility() {
    const selFacility = this.userForm.get('selectedFacility').value;
    const currFacility = this.userForm.get('facility').value;
    let facArr = [];
    if (currFacility) {
      facArr = currFacility;
      //  If facility is selected already then no need to push it again
      if (!facArr.includes(selFacility)) {
        facArr.push(selFacility);
        this.userForm.get('facility').setValue(facArr);
      }
    } else {
      facArr.push(selFacility);
      this.userForm.get('facility').setValue(facArr);
    }
    this.userForm.get('selectedFacility').setValue(null);
  }

  removeFacility(facility) {
    let facArr = this.userForm.get('facility').value;
    facArr = facArr.filter(f => f !== facility);
    this.userForm.get('facility').setValue(facArr);
  }

  back() {
    this.router.navigate(['/showUsers']);
  }
}
