import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FeatureManagementService } from 'src/app/root/services/feature-management.service';
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
    private readonly authGuard: AuthGuard
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
    this.filteredFeatureList = [];
    this.featureService.getAllFeatures().subscribe(res => {
      if (res) {
        this.mainFeatureList = res;
        this.mainFeatureList.forEach(el => {
          //  as Dashboard & Questionnaires are main features which does not have sub features so we need it to show
          if (el.menuName === 'Dashboard' ||
            el.menuName === 'Questionnaires' ||
            el.parent !== null) {
            this.filteredFeatureList.push(el);
          }
        });
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
    /* currently we are not showing main features in list so commenting 
      this code incase we need it then we can use it again else can remove it */
    // const isActionShow = this.filteredFeatureList[index]['parent'] === null ? false : true;
    // this.featureSubject.setActionShow(isActionShow);
    this.router.navigate([`editFeature/${this.filteredFeatureList[index]['id']}`],
      { queryParams: { name: this.filteredFeatureList[index]['menuName'] } });
  }
}
