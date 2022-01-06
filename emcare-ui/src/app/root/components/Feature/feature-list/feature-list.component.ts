import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { features } from 'process';
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

  constructor(
    private readonly router: Router,
    private readonly featureService: FeatureManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getAllFeatures();
  }

  getAllFeatures() {
    this.mainFeatureList = [];
    this.featureService.getAllFeatures().subscribe(res => {
      if (res) {
        this.mainFeatureList = res;
        this.filteredFeatureList = this.mainFeatureList;
      }
    });
  }

  searchFilter() {
    this.filteredFeatureList = this.mainFeatureList.filter(feature => {
      return feature.menuName?.toLowerCase().includes(this.searchString);
    })
  }

  updateFeature(index) {
    this.router.navigate([`editFeature/${this.filteredFeatureList[index]['id']}`]);
  }

}
