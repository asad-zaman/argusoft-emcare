import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-show-facility',
  templateUrl: './show-facility.component.html',
  styleUrls: ['./show-facility.component.scss']
})
export class ShowFacilityComponent implements OnInit {

  facilityArr: Array<any> = [];
  searchString;
  isAPIBusy: boolean;

  constructor(
    private readonly router: Router,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getFacilities();
  }

  getFacilities() {
    this.fhirService.getFacility().subscribe(res => {
      if (res && res['entry']) {
        res['entry'].map(el => {
          this.facilityArr.push(el.resource);
        });
      }
    });
  }

  editFacility(index) {
    this.router.navigate([`manageFacility/${this.facilityArr[index]['id']}`]);
  }

  deleteFacility(index) {
    this.fhirService.deleteFacility(this.facilityArr[index]['id']).subscribe(_res => {
      this.getFacilities();
      this.toasterService.showSuccess('Facility deleted successfully!', 'EMCARE');
    }, (_error) => {
      this.toasterService.showError('Facility could not deleted successfully!', 'EMCARE');
    });
  }

  searchFilter() {

  }
}
