import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { LocationService } from 'src/app/root/services/location.service';
import { RoleManagementService } from 'src/app/root/services/role-management.service';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { FhirService, ToasterService } from 'src/app/shared';
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
  dropdownActiveArr = [];
  isAddFeature: boolean = true;
  isEditFeature: boolean = true;
  isAllowed: boolean = true;
  selectedAreasArr = [];
  eventsSubject: Subject<boolean> = new Subject<boolean>();
  isUsernameAllowed: boolean;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly userService: UserManagementService,
    private readonly roleService: RoleManagementService,
    private readonly locationService: LocationService,
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

  manipulateLocationResponse(locations) {
    locations.forEach(el => {
      this.selectedAreasArr.push({
        id: el.id,
        string: el.hierarch
      });
    });
  }

  mapUpdateForm() {
    this.userService.getUserById(this.editId).subscribe(res => {
      if (res) {
        const data = {
          firstName: res['firstName'],
          lastName: res['lastName'],
          username: res['userName'],
        };
        this.manipulateLocationResponse(res['locations']);
        this.userForm.patchValue(data);
        this.userForm.patchValue({
          location: this.getSelectedLocations(this.selectedAreasArr)
        });
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
        location: ['']
      });
      this.userForm.addControl('username', new FormControl({ value: '', disabled: true }, Validators.required));
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
      this.toasterService.showToast('error', 'Server issue!', 'EMCARE');
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
          this.toasterService.showToast('success', 'User updated successfully!', 'EMCARE');
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
          "regRequestFrom": "web",
          "userName": this.userForm.get('username').value
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
    const isAlreadyStored = this.selectedAreasArr.find(obj => obj.id === selectedId);
    if (!isAlreadyStored) {
      this.selectedAreasArr.push({
        id: selectedId,
        string: this.getLocationStringFromArr(valueArr)
      });
    }
    this.userForm.patchValue({
      location: this.getSelectedLocations(this.selectedAreasArr)
    });
    this.eventsSubject.next(true);
  }

  getSelectedLocations(data) {
    let idArr = [];
    idArr = data.map(el => el.id);
    return idArr;
  }

  getLocationStringFromArr(arr) {
    let locationStr = "";
    arr.forEach(id => {
      if (id) {
        locationStr = locationStr + this.getLocationObjFromName(id).name + '->';
      }
    });
    return locationStr.substring(0, locationStr.length - 2);
  }

  getFormValue(event) {
    this.formData = event.formData;
    this.dropdownActiveArr = event.dropdownArr;
  }

  removeLocation(selectedLoc) {
    this.selectedAreasArr = this.selectedAreasArr.filter(loc => loc !== selectedLoc);
  }
}
