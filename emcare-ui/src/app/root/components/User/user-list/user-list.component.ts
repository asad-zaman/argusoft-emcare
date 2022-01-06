import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserManagementService } from 'src/app/root/services/user-management.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {

  mainUserList: any;
  filteredUserList: any;
  searchString: string;

  constructor(
    private readonly router: Router,
    private readonly userService: UserManagementService
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
}
