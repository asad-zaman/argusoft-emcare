import { Component, OnInit } from '@angular/core';
import { Subject } from "rxjs";
import { AuthGuard } from "src/app/auth/auth.guard";
import { ToasterService } from "src/app/shared";
import { FhirService } from "src/app/shared/services/fhir.service";

@Component({
  selector: 'app-tenant-list',
  templateUrl: './tenant-list.component.html',
  styleUrls: ['./tenant-list.component.scss']
})
export class TenantListComponent implements OnInit {

  tenants: any
  searchString: string;
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  isAPIBusy = true;
  isView = true;
  searchTermChanged: Subject<string> = new Subject<string>();
  filteredTenants;

  constructor(
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getTenants();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  manipulateResponse(res) {
    if (res) {
      this.tenants = res;
      this.filteredTenants = this.tenants;
      this.totalCount = res['totalCount'];
      this.isAPIBusy = false;
    }
  }

  getTenants() {
    this.tenants = [];
    this.fhirService.getAllTenants().subscribe(res => {
      this.manipulateResponse(res);
    });
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredTenants = this.tenants.filter(tenant => {
      return (tenant.domain?.toLowerCase().includes(lowerCasedSearchString)
        || tenant.username?.toLowerCase().includes(lowerCasedSearchString)
        || tenant.url?.toLowerCase().includes(lowerCasedSearchString));
    });
  }

  hashPassword(password: string) {
    return "*".repeat(password.length)
  }
}
