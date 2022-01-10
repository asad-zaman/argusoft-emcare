import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { UserManagementService } from 'src/app/root/services/user-management.service';

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

  constructor(
    private readonly toastr: ToastrService,
    private readonly userService: UserManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getAllSignedUpUsers();
  }

  getAllSignedUpUsers() {
    this.mainUserList = [];
    this.userService.getAllSignedUpUsers().subscribe(res => {
      if (res) {
        this.mainUserList = res;
        this.filteredUserList = this.mainUserList;
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
      this.toastr.success('User approved successfully!!', 'EMCARE');
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
    })
  }
}
