import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService } from 'src/app/shared';

@Component({
  selector: 'app-organization-list',
  templateUrl: './organization-list.component.html',
  styleUrls: ['./organization-list.component.scss']
})
export class OrganizationListComponent implements OnInit {

  orgArr: Array<any> = [];
  searchString;
  isAPIBusy: boolean = true;
  searchTermChanged: Subject<string> = new Subject<string>();
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  
  constructor(
    private readonly router: Router,
    private readonly fhirService: FhirService,
    private readonly authGuard: AuthGuard,
    private readonly translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getOrganizationByPageIndexAndSearch(this.currentPage);
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

  editOrganization(index) {
    this.router.navigate([`/manage-organization/${this.orgArr[index]['id']}`]);
  }

  resetCurrentPage() {
    this.currentPage = 0;
  }

  searchFilter() {
    this.resetCurrentPage();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {
          this.orgArr = [];
          this.fhirService.getOrganizationByPageIndexAndSearch(this.currentPage, this.searchString).subscribe(res => {
            this.manipulateRes(res);
          });
        } else {
          this.orgArr = [];
          this.getOrganizationByPageIndexAndSearch(this.currentPage);
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  manipulateRes(res) {
    if (res && res['list']) {
      this.orgArr = res['list'];
    }
  }

  onIndexChange(event) {
    this.currentPage = event;
    this.getOrganizationByPageIndexAndSearch(event - 1);
  }

  getOrganizationByPageIndexAndSearch(index) {
    this.orgArr = [];
    this.fhirService.getOrganizationByPageIndexAndSearch(index).subscribe(res => {
      this.isAPIBusy = false;
      this.manipulateRes(res);
      this.totalCount = res['totalCount'];
    });
  }

  getLabel(index) {
    const key = index === 0 ? 'Previous' : 'Next';
    let tr;
    this.translate.get(key).subscribe(res => {
      tr = res;
    });
    return tr;
  }
}
