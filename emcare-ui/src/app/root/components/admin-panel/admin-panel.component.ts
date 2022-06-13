import { Component, OnInit } from '@angular/core';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {

  userName;
  settingArr: any = [];
  templateArr: any = [];
  userSettingObj: any[] = [];
  isEdit: boolean = true;
  isView: boolean = true;

  constructor(
    private readonly toasterService: ToasterService,
    private readonly fhirService: FhirService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.userName = localStorage.getItem('Username');
    this.getAllSettings();
    this.getAllEmailTemplates();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isEdit = res.featureJSON['canEdit'];
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  getAllEmailTemplates() {
    this.fhirService.getAllEmailTemplates().subscribe(res => {
      this.templateArr.push(res[1]);
      this.templateArr.push(res[2]);
      this.templateArr.push(res[3]);
    }, () => {
      this.toasterService.showToast('error', 'Server issue!', 'EMCARE');
    });
  }

  getAllSettings() {
    this.fhirService.getAllAdminSettings().subscribe((res: any) => {
      if (res) {
        this.mapSettings(res);
        this.settingArr = res;
      }
    }, () => {
      this.toasterService.showToast('error', 'Server issue!', 'EMCARE');
    });
  }

  mapSettings(data) {
    data.forEach(el => {
      this.userSettingObj.push({ key: el.key, value: el.value === 'Active' ? true : false });
    });
  }

  updateSetting(event) {
    this.userSettingObj[event.index].value = !this.userSettingObj[event.index].value;
    let data = this.settingArr.find(e => e.key == this.userSettingObj[event.index].key);
    data['value'] = this.userSettingObj[event.index].value === true ? 'Active' : 'Inactive';
    this.fhirService.updateSetting(data).subscribe(() => {
      this.toasterService.showToast('success', 'Setting updated!', 'EMCARE');
    }, () => {
      this.toasterService.showToast('error', 'Server issue!', 'EMCARE');
    });
  }

  getTemplateData(id) {
    const obj = this.templateArr.find(e => e.id === id);
    return obj;
  }
}
