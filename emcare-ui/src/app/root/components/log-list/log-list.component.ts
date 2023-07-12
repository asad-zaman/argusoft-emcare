import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService } from 'src/app/shared';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.scss']
})
export class LogListComponent implements OnInit {

  applicationData = [];
  logs = [];
  currentActive = 0;
  token;
  isView: boolean;

  constructor(
    private readonly fhirService: FhirService,
    private readonly router: Router,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.token = JSON.parse(localStorage.getItem('access_token'));
    this.checkFeatures();
    this.getLogList();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe((res) => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  getLogList() {
    this.fhirService.getAllLogs().subscribe((res: any) => {
      if (res) {
        res.forEach((el, i) => {
          this.applicationData.push({
            id: `${i}`,
            appName: el.applicationName,
            appVersion: el.applicationVersion,
            date: el.createdOn,
            logs: el.logs,
            downloadUrl: `${environment.apiUrl}/${el.url}`
          });
        });
      }
    });
  }

  navigateToSection(id, index) {
    this.currentActive = index;
    this.router.navigate([], { fragment: id });
  }

  navigateToAddLog() {
    this.router.navigate(['/addLog']);
  }
}
