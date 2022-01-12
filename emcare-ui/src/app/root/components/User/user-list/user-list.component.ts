import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { MustMatch } from 'src/app/shared/validators/must-match.validator';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {

  mainUserList: any;
  filteredUserList: any;
  searchString: string;
  selectedUserId: string;
  resetPasswordForm!: FormGroup;
  error: any = null;
  submitted: boolean = false;
  showResetPasswordDialog: boolean = false;
  isAPIBusy: boolean = true;

  constructor(
    private readonly router: Router,
    private readonly userService: UserManagementService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getAllUsers();
  }

  getAllUsers() {
    this.mainUserList = [];
    this.userService.getAllUsers().subscribe(res => {
      if (res) {
        this.mainUserList = res;
        this.filteredUserList = this.mainUserList;
        this.isAPIBusy = false;
      }
    });
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredUserList = this.mainUserList.filter(user => {
      let roleFlag = false;
      user.realmRoles.every(role => {
        if (role.toLowerCase().includes(lowerCasedSearchString)) {
          roleFlag = true;
          return false;
        }
        return true;
      });

      return (roleFlag
        || user.id?.toLowerCase().includes(lowerCasedSearchString)
        || user.firstName?.toLowerCase().includes(lowerCasedSearchString)
        || user.lastName?.toLowerCase().includes(lowerCasedSearchString)
        || user.username?.toLowerCase().includes(lowerCasedSearchString))
    })
  }

  addUser() {
    this.router.navigate([`addUser`]);
  }

  updateUser(index) {
    this.router.navigate([`updateUser/${this.filteredUserList[index]['id']}`]);
  }

  getLocationId(data) {
    const selectedId = data;
    this.userService.getUserByLocationId(selectedId).subscribe(res => {
      if (res) {
        this.filteredUserList = [];
        this.filteredUserList = res;
      }
    })
  }

  onResetPassword(index) {
    this.selectedUserId = this.filteredUserList[index]['id'];
    this.showResetPasswordDialog = true;
    this.initResetPasswordForm();
  }

  initResetPasswordForm() {
    this.resetPasswordForm = this.formBuilder.group({
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
    }, {
      validator: MustMatch('password', 'confirmPassword')
    });
  }

  get f() {
    return this.resetPasswordForm.controls;
  }

  updatePassword() {
    this.submitted = true;
    if (this.resetPasswordForm.invalid || !!this.error) {
      return;
    }
    const user = {
      password: this.resetPasswordForm.value.password
    }
    this.userService.updatePassword(user, this.selectedUserId).subscribe(result => {
      if (result) {
        this.closeDialog();
      }
    })
  }

  closeDialog() {
    this.submitted = false;
    this.error = null;
    this.showResetPasswordDialog = false;
    this.resetPasswordForm.reset();
  }

}
