import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { LocationService } from 'src/app/root/services/location.service';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-show-facility',
  templateUrl: './show-facility.component.html',
  styleUrls: ['./show-facility.component.scss']
})
export class ShowFacilityComponent implements OnInit {

  facilityArr: Array<any> = [];
  searchString;
  isAPIBusy: boolean = true;
  locationArr: Array<any> = [];
  searchTermChanged: Subject<string> = new Subject<string>();
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;
  isDelete: boolean = true;
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  isInactive = false;

  constructor(
    private readonly router: Router,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly locationService: LocationService,
    private readonly authGuard: AuthGuard,
    private readonly translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getFacilityByPageAndSearch(this.currentPage);
    this.getAllLocations();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAdd = res.featureJSON['canAdd'];
        this.isEdit = res.featureJSON['canEdit'];
        this.isView = res.featureJSON['canView'];
        this.isDelete = res.featureJSON['canDelete'];
      }
    });
  }

  getLocationNameByID(id) {
    return this.locationArr.find(el => el.id == id).name;
  }

  capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  getAllLocations() {
    this.isAPIBusy = true;
    this.locationService.getAllLocations().subscribe((res: Array<Object>) => {
      this.isAPIBusy = false;
      if (res) {
        this.locationArr = res;
      }
    })
  }

  getFacilityByPageAndSearch(currentPage) {
    this.fhirService.getFacilityByPageAndSearch(currentPage, null, this.isInactive).subscribe(res => {
      this.isAPIBusy = false;
      this.manipulateRes(res);
    });
  }

  editFacility(index) {
    this.router.navigate([`editFacility/${this.facilityArr[index]['facilityId']}`]);
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
          this.facilityArr = [];
          this.fhirService.getFacilityByPageAndSearch(0, this.searchString, this.isInactive).subscribe(res => {
            this.manipulateRes(res);
          });
        } else {
          this.facilityArr = [];
          this.getFacilityByPageAndSearch(this.currentPage);
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  manipulateRes(res) {
    if (res) {
      this.facilityArr = res['list'];
      this.totalCount = res['totalCount']; 
    }
  }

  getLabel(index) {
    const key = index === 0 ? 'Previous' : 'Next';
    let tr;
    this.translate.get(key).subscribe(res => {
      tr = res;
    });
    return tr;
  }

  onIndexChange(event) {
    this.currentPage = event;
    if (this.searchString && this.searchString.length >= 1) {
      this.facilityArr = [];
      this.fhirService.getFacilityByPageAndSearch(event - 1, this.searchString, this.isInactive).subscribe(res => {
        this.manipulateRes(res);
      });
    } else {
      this.getFacilityByPageAndSearch(event - 1);
    }
  }

  onChangeCheckboxForFacility() {
    this.currentPage = 0;
    this.fhirService.getFacilityByPageAndSearch(this.currentPage, this.searchString, this.isInactive).subscribe(res => {
      this.isAPIBusy = false;
      this.manipulateRes(res);
    });
  }
}
