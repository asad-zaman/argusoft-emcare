import { Component, OnInit } from '@angular/core';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { ToasterService } from 'src/app/shared';
@Component({
  selector: 'app-confirm-user',
  templateUrl: './confirm-user.component.html',
  styleUrls: ['./confirm-user.component.scss']
})
export class ConfirmUserComponent implements OnInit {

  mainUserList: any;
  filteredUserList: any;
  showConfirmDialogFlag: boolean = false;
  isApproveUser: boolean = true;
  selectedUser: any;
  searchString: string;
  isAPIBusy: boolean = true;
  isView: boolean = true;

  constructor(
    private readonly toasterService: ToasterService,
    private readonly userService: UserManagementService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getAllSignedUpUsers();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  getAllSignedUpUsers() {
    this.mainUserList = [];
    this.userService.getAllSignedUpUsers().subscribe(res => {
      if (res) {
        this.mainUserList = res;
        this.filteredUserList = this.mainUserList;
        this.isAPIBusy = false;
      }
    });
  }

  onApproveUser(index) {
    this.isApproveUser = true;
    this.showConfirmDialogFlag = true;
    this.selectedUser = this.filteredUserList[index];
  }

  onDisapproveUser(index) {
    this.isApproveUser = false;
    this.showConfirmDialogFlag = true;
    this.selectedUser = this.filteredUserList[index];
  }

  authorizeUser() {
    const data = {
      "userId": this.selectedUser.id,
      "isEnabled": this.isApproveUser
    }
    this.userService.updateUserStatus(data).subscribe(res => {
      this.toasterService.showToast('success',
        this.isApproveUser ? 'User approved successfully!' : 'User disapproved successfully!', 'EMCARE');
      this.getAllSignedUpUsers();
    });
    this.showConfirmDialogFlag = false;
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
        || user.firstName?.toLowerCase().includes(lowerCasedSearchString)
        || user.lastName?.toLowerCase().includes(lowerCasedSearchString)
        || user.email?.toLowerCase().includes(lowerCasedSearchString))
    });
  }

  getFacilityStr(facilityData) {
    return `${facilityData.facilityName} - ${facilityData.locationName}`;
  }
}
