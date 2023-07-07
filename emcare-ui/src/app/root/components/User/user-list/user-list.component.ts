import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { ToasterService } from 'src/app/shared';
import { MustMatch } from 'src/app/shared/validators/must-match.validator';
import { AuthGuard } from 'src/app/auth/auth.guard';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
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
  selectedId: any;
  searchTermChanged: Subject<string> = new Subject<string>();
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;
  isInactive: boolean = false;
  showStatusDialog: boolean = false;
  currUSer;

  constructor(
    private readonly router: Router,
    private readonly userService: UserManagementService,
    private readonly formBuilder: FormBuilder,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getUsersBasedOnData(this.currentPage);
  }

  getUsersBasedOnData(pageIndex) {
    const filterData = {
      locationId: this.selectedId,
      filterValueForActiveInactive: this.isInactive,
      searchString: this.searchString
    };
    this.userService.getUsersByData(pageIndex, filterData).subscribe(res => {
      if (res) {
        this.manipulateResponse(res);
      }
    });
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe((res) => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAdd = res.featureJSON['canAdd'];
        this.isEdit = res.featureJSON['canEdit'];
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  manipulateResponse(res) {
    if (res && res['list']) {
      this.filteredUserList = [];
      this.mainUserList = res['list'];
      this.filteredUserList = this.mainUserList;
      this.totalCount = res['totalCount'];
      this.isAPIBusy = false;
    }
  }

  onIndexChange(event) {
    this.currentPage = event;
    this.getUsersBasedOnData(event - 1);
  }

  searchFilter() {
    this.resetPageIndex();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(debounceTime(1000), distinctUntilChanged()).subscribe((_term) => {
        this.getUsersBasedOnData(this.currentPage);
      });
    }
    this.searchTermChanged.next(this.searchString);
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
      this.resetPageIndex();
      this.getUsersBasedOnData(this.currentPage);
    } else {
      this.toasterService.showToast('info', 'Please select Location!', 'EMCARE');
    }
  }

  onResetPassword(index) {
    this.selectedUserId = this.filteredUserList[index]['id'];
    this.showResetPasswordDialog = true;
    this.initResetPasswordForm();
  }

  initResetPasswordForm() {
    this.resetPasswordForm = this.formBuilder.group(
      {
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required],
      },
      {
        validator: MustMatch('password', 'confirmPassword'),
      }
    );
  }

  get getFormConfrols() {
    return this.resetPasswordForm.controls;
  }

  updatePassword() {
    this.submitted = true;
    if (this.resetPasswordForm.invalid || !!this.error) {
      return;
    }
    const user = {
      password: this.resetPasswordForm.value.password,
    };
    this.userService
      .updatePassword(user, this.selectedUserId)
      .subscribe((result) => {
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

  getFacilityNames(data) {
    if (data && data.length > 0) {
      let facilityStrArr = data.map((d) => {
        return `${d.facilityName} - ${d.organizationName}`;
      });
      return facilityStrArr;
    } else {
      return 'NA';
    }
  }

  getLocation(data) {
    if (data && data.length > 0) {
      let locationStrArr = data.map((d) => {
        return `${d.locationName}`;
      });
      return locationStrArr;
    } else {
      return 'NA';
    }
  }

  clearFilter(event) {
    if (event) {
      this.resetPageIndex();
      this.selectedId = null;
      this.getUsersBasedOnData(this.currentPage);
    }
  }

  onChangeCheckboxForUser() {
    this.currentPage = 0;
    this.getUsersBasedOnData(this.currentPage);
  }

  onChangeStatus(i) {
    this.showStatusDialog = true;
    this.currUSer = this.filteredUserList[i];
  }

  changeUSerStatus() {
    const data = {
      userId: this.currUSer.id,
      isEnabled: !this.currUSer['enabled'],
    };
    this.userService.updateUserStatus(data).subscribe((res) => {
      this.showStatusDialog = false;
      if (this.isInactive) {
        this.getUsersBasedOnData(this.currentPage);
      } else {
        const ind = this.filteredUserList.findIndex(
          (el) => el.id === this.currUSer.id
        );
        this.filteredUserList[ind]['enabled'] = !this.filteredUserList[ind]['enabled'];
      }
      this.toasterService.showToast(
        'success',
        'User status changed successfully!',
        'EMCARE'
      );
    });
  }
}
