import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly userService: UserManagementService,
    private readonly roleService: RoleManagementService,
    private readonly locationService: LocationService,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    this.getAllLocations();
    if (this.editId) {
      this.isEdit = true;
      this.initUpdateForm();
    } else {
      this.getRoles();
    }
    this.initUserForm();
  }

  initUpdateForm() {
    this.userService.getUserById(this.editId).subscribe(res => {
      if (res) {
        const data = {
          firstName: res['firstName'],
          lastName: res['lastName'],
          location: res['locationId']
        };
        this.userForm.patchValue(data);
      }
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
    });
  }

  getAllLocations() {
    this.locationService.getAllLocations().subscribe(res => {
      if (res) {
        this.locationArr = res;
      }
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
          "locationId": this.userForm.get('location').value,
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
          "locationId": this.userForm.get('location').value,
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
}
