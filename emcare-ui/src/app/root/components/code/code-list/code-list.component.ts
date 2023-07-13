import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-code-list',
  templateUrl: './code-list.component.html',
  styleUrls: ['./code-list.component.scss']
})
export class CodeListComponent implements OnInit {

  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  searchString: string;
  isAPIBusy: boolean = true;
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;
  codeArr = [];
  searchTermChanged: Subject<string> = new Subject<string>();

  constructor(
    private readonly router: Router,
    private readonly toasterService: ToasterService,
    private readonly fhirService: FhirService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getCodeList();
    this.checkFeatures();
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

  getCodeList() {
    this.codeArr = [];
    this.fhirService.getCodes(this.currentPage).subscribe((res: any) => {
      if (res) {
        this.manipulateResponse(res);
        this.totalCount = res['totalCount'];
        this.isAPIBusy = false;
      }
    }, () => {
      this.toasterService.showToast('error', 'Server issue!', 'EMCARE');
    });
  }

  manipulateResponse(res) {
    this.codeArr = res['list'];
  }

  addCode() {
    this.router.navigate([`manageCode`]);
  }

  editCode(code) {
    this.router.navigate([`manageCode/${code['codeId']}`]);
  }

  searchFilter() {
    this.resetPageIndex();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {
          this.codeArr = [];
          this.fhirService.getCodes(this.currentPage, this.searchString).subscribe(res => {
            this.manipulateResponse(res);
          });
        } else {
          this.getCodeByPageIndex(this.currentPage);
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  resetPageIndex() {
    this.currentPage = 0;
  }

  getCodeByPageIndex(index) {
    this.codeArr = [];
    this.fhirService.getCodes(index).subscribe(res => {
      this.manipulateResponse(res);
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    if (this.searchString && this.searchString.length >= 1) {
      this.codeArr = [];
      this.fhirService.getCodes(event - 1, this.searchString).subscribe(res => {
        this.manipulateResponse(res);
      });
    } else {
      this.getCodeByPageIndex(event - 1);
    }
  }
}
