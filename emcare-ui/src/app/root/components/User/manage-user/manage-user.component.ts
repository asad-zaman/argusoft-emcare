import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { LocationService } from 'src/app/root/services/location.service';
import { RoleManagementService } from 'src/app/root/services/role-management.service';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { ToasterService } from 'src/app/shared';
import { MustMatch } from 'src/app/shared/validators/must-match.validator';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.scss']
})
export class ManageUserComponent implements OnInit {

  userForm: FormGroup;
  isEdit: boolean = false;
  editId: string;
  roles: any;
  locationArr: any = [];
  submitted = false;
  formData;
  locationIdArr: Array<any> = [];
  dropdownActiveArr = [];
  isAddFeature: boolean = true;
  isEditFeature: boolean = true;
  isAllowed: boolean = true;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly userService: UserManagementService,
    private readonly roleService: RoleManagementService,
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
    this.checkEditParams();
    this.initUserForm();
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

  checkEditParams() {
    if (this.editId) {
      this.isEdit = true;
    } else {
      this.isEdit = false;
    }
  }

  getLocationIdArr(locationsArr) {
    return locationsArr.map(l => l.id);
  }

  mapUpdateForm() {
    this.userService.getUserById(this.editId).subscribe(res => {
      if (res) {
        const currentLevelIdArr = this.getLocationIdArr(res['locations']);
        const isMultiple = currentLevelIdArr.length > 1;
        const data = {
          firstName: res['firstName'],
          lastName: res['lastName'],
          location: currentLevelIdArr
        };
        if (res['locations'].length > 0) {
          this.locationService.getParentLocationsById(res['locations'][0].id).subscribe((parentRes: Array<any>) => {
            this.locationIdArr = parentRes.map(el => el.id).reverse();
            if (isMultiple) {
              this.locationIdArr.pop();
              this.locationIdArr.push(currentLevelIdArr);
            }
          });
        }
        this.userForm.patchValue(data);
      }
    });
  }

  getLocationObjFromName(id) {
    return this.locationArr.find(loc => {
      return loc.id == Number(id)
    });
  }

  initUserForm() {
    if (this.isEdit) {
      this.userForm = this.formBuilder.group({
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        location: ['', Validators.required]
      });
    } else {
      this.userForm = this.formBuilder.group({
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required],
        role: ['', [Validators.required]],
        location: ['', Validators.required]
      }, {
        validator: MustMatch('password', 'confirmPassword')
      });
    }
  }

  getRoles() {
    this.roleService.getAllRoles().subscribe(res => {
      if (res) {
        this.roles = res;
      }
    }, () => {
      this.toasterService.showError('Server issue!', 'EMCARE');
    });
  }

  getAllLocations() {
    this.locationService.getAllLocations().subscribe(res => {
      if (res) {
        this.locationArr = res;
        if (this.isEdit) {
          this.mapUpdateForm();
        } else {
          this.getRoles();
        }
      }
    }, () => {
      this.toasterService.showError('Server issue!', 'EMCARE');
    });
  }

  get f() {
    return this.userForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.userForm.valid) {
      if (this.isEdit) {
        const data = {
          "firstName": this.userForm.get('firstName').value,
          "lastName": this.userForm.get('lastName').value,
          "locationIds": this.userForm.get('location').value,
          "regRequestFrom": "web"
        }
        this.userService.updateUser(data, this.editId).subscribe(res => {
          this.toasterService.showSuccess('User updated successfully!', 'EMCARE');
          this.showUser();
        });
      } else {
        const data = {
          "firstName": this.userForm.get('firstName').value,
          "lastName": this.userForm.get('lastName').value,
          "email": this.userForm.get('email').value,
          "password": this.userForm.get('password').value,
          "roleName": this.userForm.get('role').value,
          "locationIds": this.userForm.get('location').value,
          "regRequestFrom": "web"
        }
        this.userService.createUser(data).subscribe(res => {
          this.toasterService.showSuccess('User added successfully!', 'EMCARE');
          this.showUser();
        });
      }
    }
  }

  showUser() {
    this.router.navigate([`showUsers`]);
  }

  saveLocationData() {
    const valueArr = [
      this.formData.country, this.formData.state,
      this.formData.city, this.formData.region,
      this.formData.other
    ];
    let selectedId;
    for (let index = this.dropdownActiveArr.length - 1; index >= 0; index--) {
      const data = this.dropdownActiveArr[index];
      //  if value is not selected and showing --select-- in dropdown then the parent valus should be emitted as selectedId
      if (data && (valueArr[index] !== "") && !selectedId) {
        selectedId = valueArr[index];
      }
    }
    this.userForm.patchValue({
      location: selectedId
    });
  }

  getFormValue(event) {
    this.formData = event.formData;
    this.dropdownActiveArr = event.dropdownArr
  }
}
