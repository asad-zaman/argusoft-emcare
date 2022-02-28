import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FeatureManagementService } from 'src/app/root/services/feature-management.service';
import { RoleManagementService } from 'src/app/root/services/role-management.service';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { ToasterService } from 'src/app/shared';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
@Component({
  selector: 'app-manage-feature',
  templateUrl: './manage-feature.component.html',
  styleUrls: ['./manage-feature.component.scss']
})
export class ManageFeatureComponent implements OnInit {

  featureConfigList: any;
  featureName: string = '';
  featureId: string;
  userList: any = [];
  roleList: any = [];
  selectedUser: any = null;
  selectedRole: any = null;
  isAPIBusy: boolean = true;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly userService: UserManagementService,
    private readonly roleService: RoleManagementService,
    private readonly featureService: FeatureManagementService,
    private readonly toasterService: ToasterService,
    private readonly authenticationService: AuthenticationService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.featureId = this.route.snapshot.paramMap.get('id');
    this.route.queryParams.subscribe(params => {
      this.featureName = params.name;
    })
    this.getFeatureConfig();
  }

  getFeatureConfig() {
    this.featureService.getFeatureConfigById(this.featureId).subscribe(res => {
      if (res) {
        this.featureConfigList = res;
        this.isAPIBusy = false;
        this.getUsers();
        this.getRoles();
      }
    })
  }

  getUsers() {
    this.userService.getAllUsers().subscribe(res => {
      if (res) {
        this.userList = res;
        this.userList = this.userList.filter(
          (user: { id: any; }) => !this.featureConfigList.map(featureConfig => featureConfig.userId).includes(user.id)
        );
      }
    })
  }

  getRoles() {
    this.roleService.getAllRoles().subscribe(res => {
      if (res) {
        this.roleList = res;
        this.roleList = this.roleList.filter(
          (role: { id: any; }) => !this.featureConfigList.map(featureConfig => featureConfig.roleId).includes(role.id)
        );
      }
    })
  }

  deleteFeatureConfig(index) {
    this.featureService.deleteFeatureConfig(this.featureConfigList[index]['id']).subscribe(res => {
      this.toasterService.showSuccess('Feature deleted successfully!', 'EMCARE');
      this.prerequisite();
      this.getFeatureList();
    });
  }

  AddFeatureConfig() {
    const data = {
      "menuId": this.featureId,
      "userId": this.selectedUser && this.selectedUser.id,
      "roleId": this.selectedRole && this.selectedRole.id,
      "featureJson": "{\"canAdd\":true,\"canEdit\":true,\"canView\":true,\"canDelete\":true}"
    }
    this.featureService.addFeatureConfig(data).subscribe(_res => {
      this.toasterService.showSuccess('Feature added successfully!', 'EMCARE');
      this.selectedUser = null;
      this.selectedRole = null;
      this.prerequisite();
      this.getFeatureList();
    })
  }

  getFeatureList() {
    this.authenticationService.getLoggedInUser().subscribe(res => {
      if (res) {
        this.authenticationService.setFeatures(res.feature.map(f => f.menu_name));
      }
    })
  }
}
