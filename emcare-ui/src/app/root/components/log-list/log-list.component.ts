import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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

  constructor(
    private readonly fhirService: FhirService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.token = JSON.parse(localStorage.getItem('access_token'));
    this.getLogList();
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
