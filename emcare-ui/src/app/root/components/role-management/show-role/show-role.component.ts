import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { RoleManagementService } from 'src/app/root/services/role-management.service';
@Component({
  selector: 'app-show-role',
  templateUrl: './show-role.component.html',
  styleUrls: ['./show-role.component.scss']
})
export class ShowRoleComponent implements OnInit {

  rolesArr: any;
  filteredRoles: any;
  searchString: string;
  isAPIBusy: boolean = true;
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;

  constructor(
    private readonly router: Router,
    private readonly roleService: RoleManagementService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getRoles();
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

  getRoles() {
    this.rolesArr = [];
    this.roleService.getAllRoles().subscribe(res => {
      if (res) {
        this.rolesArr = res;
        this.filteredRoles = this.rolesArr;
        this.isAPIBusy = false;
      }
    });
  }

  addRole() {
    this.router.navigate([`addRole`]);
  }

  editRole(index) {
    this.router.navigate([`editRole/${this.filteredRoles[index]['id']}`]);
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredRoles = this.rolesArr.filter(role => {
      return (role.name?.toLowerCase().includes(lowerCasedSearchString)
        || role.description?.toLowerCase().includes(lowerCasedSearchString))
    });
  }
}
