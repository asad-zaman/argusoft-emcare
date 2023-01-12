import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-indicator-list',
  templateUrl: './indicator-list.component.html',
  styleUrls: ['./indicator-list.component.scss']
})
export class IndicatorListComponent implements OnInit {

  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  searchString: string;
  isAPIBusy: boolean = true;
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;
  indicatorArr = [];
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
    this.getIndicatorList();
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

  getIndicatorList() {
    this.indicatorArr = [];
    this.fhirService.getIndicators(this.currentPage).subscribe((res: any) => {
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
    this.indicatorArr = res['list'];
  }

  addIndicator() {
    this.router.navigate([`addIndicator`]);
  }

  searchFilter() {
    this.resetPageIndex();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {
          this.indicatorArr = [];
          this.fhirService.getIndicators(this.currentPage, this.searchString).subscribe(res => {
            this.manipulateResponse(res);
          });
        } else {
          this.getIndicatorsByPageIndex(this.currentPage);
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  resetPageIndex() {
    this.currentPage = 0;
  }

  getIndicatorsByPageIndex(index) {
    this.indicatorArr = [];
    this.fhirService.getIndicators(index).subscribe(res => {
      this.manipulateResponse(res);
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    if (this.searchString && this.searchString.length >= 1) {
      this.indicatorArr = [];
      this.fhirService.getIndicators(event - 1, this.searchString).subscribe(res => {
        this.manipulateResponse(res);
      });
    } else {
      this.getIndicatorsByPageIndex(event - 1);
    }
  }

  editIndicator(indicator) {
    this.router.navigate([`/editIndicator/${indicator.indicatorId}`])
  }
}
