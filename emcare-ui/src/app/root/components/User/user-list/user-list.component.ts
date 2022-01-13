import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { ToasterService } from 'src/app/shared';
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
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  selectedUserId: string;
  resetPasswordForm!: FormGroup;
  error: any = null;
  submitted: boolean = false;
  showResetPasswordDialog: boolean = false;
  isAPIBusy: boolean = true;
  isLocationFilterOn: boolean = false;
  selectedId: any;

  constructor(
    private readonly router: Router,
    private readonly userService: UserManagementService,
    private readonly formBuilder: FormBuilder,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getUsersByPageIndex(this.currentPage);
  }

  getUsersByPageIndex(index) {
    this.mainUserList = [];
    this.userService.getUsersByPage(index).subscribe(res => {
      if (res && res['list']) {
        this.mainUserList = res['list'];
        this.filteredUserList = this.mainUserList;
        this.totalCount = res['totalCount'];
        this.isAPIBusy = false;
      }
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    if (this.isLocationFilterOn) {
      this.getUsersBasedOnLocationAndPageIndex(event - 1);
    } else {
      this.getUsersByPageIndex(event - 1);
    }
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

  resetPageIndex() {
    this.currentPage = 0;
  }

  getLocationId(data) {
    this.selectedId = data;
    if (this.selectedId) {
      this.isLocationFilterOn = true;
      this.resetPageIndex();
      const pageIndex = this.currentPage === 0 ? this.currentPage : this.currentPage - 1;
      this.getUsersBasedOnLocationAndPageIndex(pageIndex);
    } else {
      this.toasterService.showError('Please select Location!', 'EMCARE')
    }
  }

  getUsersBasedOnLocationAndPageIndex(pageIndex) {
    this.userService.getUsersByLocationAndPageIndex(this.selectedId, pageIndex).subscribe(res => {
      if (res) {
        this.filteredUserList = [];
        this.filteredUserList = res['list'];
        this.totalCount = res['totalCount'];
        this.isAPIBusy = false;
      }
    });
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
    });
  }

  closeDialog() {
    this.submitted = false;
    this.error = null;
    this.showResetPasswordDialog = false;
    this.resetPasswordForm.reset();
  }
}
