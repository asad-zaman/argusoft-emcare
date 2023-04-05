import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FhirService } from 'src/app/shared';

@Component({
  selector: 'app-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.scss']
})
export class LogListComponent implements OnInit {

  applicationData = [];
  logs = [];

  constructor(
    private readonly fhirService: FhirService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
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
            downloadUrl: `${window.origin}/${el.url}`
          });
        });
      }
    });
  }

  navigateToSection(id) {
    this.router.navigate([], { fragment: id });
  }

  navigateToAddLog() {
    this.router.navigate(['/addLog']);
  }
}
