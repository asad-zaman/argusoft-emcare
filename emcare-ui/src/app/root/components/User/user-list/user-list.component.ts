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
  searchTermChanged: Subject<string> = new Subject<string>();
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;

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
    this.getUsersByPageIndex(this.currentPage);
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAdd = res.featureJSON['canAdd'];
        this.isEdit = res.featureJSON['canEdit'];
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  manipulateResponse(res) {
    if (res && res['list']) {
      this.mainUserList = res['list'];
      this.filteredUserList = this.mainUserList;
      this.totalCount = res['totalCount'];
      this.isAPIBusy = false;
    }
  }

  getUsersByPageIndex(index) {
    this.mainUserList = [];
    this.userService.getUsersByPage(index).subscribe(res => {
      this.manipulateResponse(res);
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
    this.resetPageIndex();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {
          this.mainUserList = [];
          this.userService.getUsersByPage(this.currentPage, this.searchString).subscribe(res => {
            this.manipulateResponse(res);
          });
        } else {
          this.getUsersByPageIndex(this.currentPage);
        }
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
      this.isLocationFilterOn = true;
      this.resetPageIndex();
      this.getUsersBasedOnLocationAndPageIndex(this.currentPage);
    } else {
      this.toasterService.showToast('info', 'Please select Location!', 'EMCARE')
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

  getLocationNames(data) {
    if (data && data.length > 0) {
      let locationStr = '';
      data.map((d, index) => {
        locationStr += d.name;
        if (index !== data.length - 1) {
          locationStr += ' ';
        }
      });
      return locationStr;
    } else {
      return 'NA';
    }
  }
}
