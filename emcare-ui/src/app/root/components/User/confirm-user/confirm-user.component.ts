import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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
  searchString:string;

  constructor(
    private readonly router: Router,
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
      this.getAllSignedUpUsers();
    });
    this.showConfirmDialogFlag = false;
  }

  searchFilter() {
    this.filteredUserList = this.mainUserList.filter( user => {
      let roleFlag = false;
      user.realmRoles.every(role => {
        if(role.includes(this.searchString)){
          roleFlag = true;
          return false;
        }
        return true;
      });

      return (roleFlag 
          ||  user.id?.includes(this.searchString) 
          ||  user.firstName?.includes(this.searchString)
          ||  user.lastName?.includes(this.searchString)
          ||  user.username?.includes(this.searchString))
    })
  }

}
