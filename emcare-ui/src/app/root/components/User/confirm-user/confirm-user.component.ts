import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserManagementService } from 'src/app/root/services/user-management.service';

@Component({
  selector: 'app-confirm-user',
  templateUrl: './confirm-user.component.html',
  styleUrls: ['./confirm-user.component.scss']
})
export class ConfirmUserComponent implements OnInit {

  userList: any;
  showConfirmDialogFlag: boolean = false;
  isApproveUser: boolean = true;
  selectedUser: any;

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
    this.userList = [];
    this.userService.getAllSignedUpUsers().subscribe(res => {
      if (res) {
        this.userList = res;
      }
    });
  }

  onApproveUser(index) {
    this.isApproveUser = true;
    this.showConfirmDialogFlag = true;
    this.selectedUser = this.userList[index];
  }

  onDisapproveUser(index) {
    this.isApproveUser = false;
    this.showConfirmDialogFlag = true;
    this.selectedUser = this.userList[index];
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

}
