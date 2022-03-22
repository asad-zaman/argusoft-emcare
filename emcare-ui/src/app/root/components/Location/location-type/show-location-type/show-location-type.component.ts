import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { LocationService } from 'src/app/root/services/location.service';
import { ToasterService } from 'src/app/shared';
@Component({
  selector: 'app-show-location-type',
  templateUrl: './show-location-type.component.html',
  styleUrls: ['./show-location-type.component.scss']
})
export class ShowLocationTypeComponent implements OnInit {

  locationTypeArr: any;
  filteredLocationTypes: any;
  searchString: string;
  isAPIBusy: boolean = true;
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;

  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getLocationTypes();
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

  getLocationTypes() {
    this.locationTypeArr = [];
    this.locationService.getAllLocationTypes().subscribe(res => {
      if (res) {
        this.locationTypeArr = res;
        this.filteredLocationTypes = this.locationTypeArr;
        this.isAPIBusy = false;
      }
    });
  }

  addLocationType() {
    this.router.navigate([`addLocationType`]);
  }

  editLocationType(index) {
    this.router.navigate([`editLocationType/${this.filteredLocationTypes[index]['hierarchyType']}`]);
  }

  deleteLocationType(index) {
    this.locationService.deleteLocationTypeById(this.filteredLocationTypes[index]['hierarchyType']).subscribe(res => {
      this.toasterService.showSuccess('Location Deleted successfully!', 'EMCARE');
      this.getLocationTypes();
    });
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredLocationTypes = this.locationTypeArr.filter(locationType => {
      return (locationType.name?.toLowerCase().includes(lowerCasedSearchString)
        || locationType.code?.toLowerCase().includes(lowerCasedSearchString));
    });
  }
}
