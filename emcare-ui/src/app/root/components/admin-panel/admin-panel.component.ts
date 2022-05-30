import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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

  constructor(
    private readonly toasterService: ToasterService,
    private readonly fhirService: FhirService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.userName = localStorage.getItem('Username');
    this.getAllSettings();
    this.getAllEmailTemplates();
  }

  getAllEmailTemplates() {
    this.fhirService.getAllEmailTemplates().subscribe(res => {
      this.templateArr.push(res[1]);
      this.templateArr.push(res[2]);
      this.templateArr.push(res[3]);
    }, () => {
      this.toasterService.showError('Server issue!, EMCARE');
    });
  }

  getAllSettings() {
    this.fhirService.getAllAdminSettings().subscribe((res: any) => {
      if (res) {
        this.mapSettings(res);
        this.settingArr = res;
        console.log(res);
      }
    }, () => {
      this.toasterService.showError('Server issue!, EMCARE');
    });
  }

  mapSettings(data) {
    data.forEach(el => {
      this.userSettingObj.push({ settingType: el.settingType, status: el.settingStatus });
      // if (el.settingType === 'Send Confirmation Email') {
      //   this.userSettingObj[0] = { setting: el.settingType, status: el.settingStatus };
      // } if (el.settingType === 'Welcome Email') {
      //   this.userSettingObj[1] = { setting: el.settingType, status: el.settingStatus };
      // } if (el.settingType === 'Registration Email As Username') {
      //   this.userSettingObj[2] = el.settingStatus;
      // }
    });
    console.log(this.userSettingObj);
  }

  updateSetting(event) {
    // console.log(this.userSettingObj);
    let settingType;
    if (event.index === 0) {
      settingType = 'Send Confirmation Email';
    } else if(event.index === 1) {
      settingType = 'Welcome Email';
    } else if(event.index === 2){
      settingType = 'Registration Email As Username';
    }
    let i = this.userSettingObj.findIndex(e => e.settingType === settingType);
    this.userSettingObj[i].status = !this.userSettingObj[i].status;
    let data = this.settingArr.find(e => e.settingType == this.userSettingObj[i].settingType);
    data['settingStatus'] = this.userSettingObj[i].status;
    // console.log(data);
    // this.fhirService.updateSetting(data).subscribe(() => {
    //   this.toasterService.showSuccess('Setting updated!', 'EMCARE');
    // }, () => {
    //   this.toasterService.showError('Server issue!', 'EMCARE');
    // });
    // console.log(this.userSettingObj);
  }

  getTemplateData(id) {
    const obj = this.templateArr.find(e => e.id === id);
    return obj;
  }
}
