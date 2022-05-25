import { Component, OnInit } from '@angular/core';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {

  userName;
  userWelcome = false;
  userConfirmation = false;
  activeState: boolean[] = [false, false, false];
  settingArr: any = [];
  templateArr: any = [];

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
      console.log(this.templateArr);
    }, () => {
      this.toasterService.showError('Server issue!, EMCARE');
    });
  }

  getAllSettings() {
    this.fhirService.getAllAdminSettings().subscribe(res => {
      if (res) {
        this.mapSettings(res);
        this.settingArr = res;
      }
    }, () => {
      this.toasterService.showError('Server issue!, EMCARE');
    });
  }

  mapSettings(data) {
    data.forEach(el => {
      if (el.settingType === 'Welcome Email') {
        this.activeState[0] = el.settingStatus;
      } else if (el.settingType === 'Send Confirmation Email') {
        this.activeState[1] = el.settingStatus;
      } else if (el.settingType === 'Registration Email As Username') {
        this.activeState[2] = el.settingStatus;
      }
    });
  }

  toggle(index: number) {
    this.activeState[index] = !this.activeState[index];
    // this.updateSetting(index);
  }

  checkToast(index) {
    if (!this.activeState[index])
      this.toasterService.showInfo('Enable Toggle first to view template!', 'EMCARE');
    else {
      this.activeState[index] = !this.activeState[index];
      // this.updateSetting(index);
    }
  }

  updateSetting(index) {
    let data = this.settingArr[index]
    data['settingStatus'] = this.activeState[index];
    this.fhirService.updateSetting(data).subscribe(() => {
      this.toasterService.showSuccess('Setting updated!', 'EMCARE');
    }, () => {
      this.toasterService.showError('Server issue!', 'EMCARE');
    })
  }

  getTemplateData(id) {
    const obj = this.templateArr.find(e => e.id === id);
    console.log(obj);
    return obj;
  }
}
