import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FeatureManagementService } from 'src/app/root/services/feature-management.service';
import { RoleManagementService } from 'src/app/root/services/role-management.service';
import { UserManagementService } from 'src/app/root/services/user-management.service';
import { ToasterService } from 'src/app/shared';
import { AuthenticationService } from 'src/app/shared/services/authentication.service';
import { appConstants } from 'src/app/app.config';

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
  sepFeatureForm: FormGroup;
  featureArr = ['canAdd', 'canEdit', 'canView', 'canDelete'];
  isAdd = true;
  isDelete = true;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly userService: UserManagementService,
    private readonly roleService: RoleManagementService,
    private readonly featureService: FeatureManagementService,
    private readonly toasterService: ToasterService,
    private readonly authenticationService: AuthenticationService,
    private readonly formBuilder: FormBuilder,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.featureId = this.route.snapshot.paramMap.get('id');
    this.route.queryParams.subscribe(params => {
      this.featureName = params.name;
    })
    this.getFeatureConfig();
    this.initFeatureForm();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAdd = res.featureJSON['canAdd'];
        this.isDelete = res.featureJSON['canDelete'];
      }
    });
  }

  initFeatureForm() {
    this.sepFeatureForm = this.formBuilder.group({
      add: ['', []],
      edit: ['', []],
      view: ['', []],
      delete: ['', []]
    });
  }

  getArrOfControls(obj) {
    const tempArr = [];
    for (let el in obj) {
      if (obj[el] === true) {
        tempArr.push(el);
      }
    }
    return tempArr;
  }

  getFeatureConfig() {
    this.featureService.getFeatureConfigById(this.featureId).subscribe(res => {
      if (res) {
        this.featureConfigList = res;
        for (let el in this.featureConfigList) {
          this.featureConfigList[el]['selectedFeatures'] = this.getArrOfControls(JSON.parse(res[el]['featureJson']));
        }
        this.isAPIBusy = false;
        this.getUsers();
        this.getRoles();
      }
    });
  }

  onCheckboxClicked(index) {
    let featureJSON = {};
    this.featureConfigList[index]['selectedFeatures'].forEach(element => {
      featureJSON[element] = true;
    });
    this.featureArr.forEach(f => {
      if (!featureJSON[f]) {
        featureJSON[f] = false;
      }
    });
    this.featureConfigList[index]['featureJson'] = JSON.stringify(featureJSON);
    const data = {
      "id": this.featureConfigList[index]['id'],
      "userId": this.featureConfigList[index]['userId'],
      "userName": this.featureConfigList[index]['userName'],
      "roleId": this.featureConfigList[index]['roleId'],
      "roleName": this.featureConfigList[index]['roleName'],
      "featureJson": this.featureConfigList[index]['featureJson'],
      "menuId": this.featureConfigList[index]['menuId']
    }
    this.featureService.updateFeatureConfig(data).subscribe(() => {
      this.toasterService.showToast('success', 'Feature changes have been saved!', 'EMCARE');
      /*  on change of anu particular feature api should be called again and features should
        be set again so that we can manage features properly  */
      this.getFeatureList();
    });
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
      this.toasterService.showToast('success', 'Feature deleted successfully!', 'EMCARE');
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
      this.toasterService.showToast('success', 'Feature added successfully!', 'EMCARE');
      this.selectedUser = null;
      this.selectedRole = null;
      this.prerequisite();
      this.getFeatureList();
    });
  }

  getFeatureList() {
    this.authenticationService.getLoggedInUser().subscribe(res => {
      if (res) {
        const featureObj = { feature: res['feature'] };
        localStorage.setItem(appConstants.localStorageKeys.userFeatures, JSON.stringify(featureObj));
        this.authenticationService.setFeatures(res['feature']);
      }
    });
  }
}
