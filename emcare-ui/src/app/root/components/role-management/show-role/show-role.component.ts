import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RoleManagementService } from 'src/app/root/services/role-management.service';

@Component({
  selector: 'app-show-role',
  templateUrl: './show-role.component.html',
  styleUrls: ['./show-role.component.scss']
})
export class ShowRoleComponent implements OnInit {

  rolesArr: any;

  constructor(
    private readonly router: Router,
    private readonly roleService: RoleManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getRoles();
  }

  getRoles() {
    this.roleService.getAllRoles().subscribe(res => {
      if(res) {
        this.rolesArr = res;
      }
    });
  }

  addRole() {
    this.router.navigate([`addRole`]);
  }

  editRole(index) {
    this.router.navigate([`editRole/${this.rolesArr[index]['id']}`]);
  }


}
