import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FeatureManagementService } from 'src/app/root/services/feature-management.service';
import { ToasterService } from 'src/app/shared';
@Component({
  selector: 'app-feature-list',
  templateUrl: './feature-list.component.html',
  styleUrls: ['./feature-list.component.scss']
})
export class FeatureListComponent implements OnInit {

  mainFeatureList: any;
  filteredFeatureList: any;
  searchString: string;
  isAPIBusy: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;

  constructor(
    private readonly router: Router,
    private readonly featureService: FeatureManagementService,
    private readonly authGuard: AuthGuard,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getAllFeatures();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isEdit = res.featureJSON['canEdit'];
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  getAllFeatures() {
    this.mainFeatureList = [];
    this.featureService.getAllFeatures().subscribe(res => {
      if (res) {
        this.mainFeatureList = res;
        this.filteredFeatureList = this.mainFeatureList;
        this.isAPIBusy = false;
      }
    });
  }

  searchFilter() {
    this.filteredFeatureList = this.mainFeatureList.filter(feature => {
      return feature.menuName?.toLowerCase().includes(this.searchString);
    })
  }

  updateFeature(index) {
    this.router.navigate([`editFeature/${this.filteredFeatureList[index]['id']}`],
      { queryParams: { name: this.filteredFeatureList[index]['menuName'] } });
  }
}
