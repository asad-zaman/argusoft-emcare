import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FhirService, ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-duplicate-patients',
  templateUrl: './duplicate-patients.component.html',
  styleUrls: ['./duplicate-patients.component.scss']
})
export class DuplicatePatientsComponent implements OnInit {

  searchString: string;
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  isAPIBusy: boolean = true;
  searchTermChanged: Subject<string> = new Subject<string>();
  isView: boolean = true;
  duplicatePatientArr = [];
  tempararyPatientData = [];
  selectedPatientIndex = null;

  constructor(
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getAllDuplicatePatientEntries();
  }

  getAllDuplicatePatientEntries() {
    this.fhirService.getAllDuplicatePatientEntries().subscribe((res: Array<Array<Object>>) => {
      if (res) {
        this.duplicatePatientArr = res;
      }
    }, (_error) => {
      this.toasterService.showToast('warn', 'API issue!', 'EMCARE');
    })
  }

  resetPageIndex() {
    this.currentPage = 0;
  }

  // toDO
  searchFilter() {
    this.resetPageIndex();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {

        } else {

        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  deletePatient(index) {
    console.log('deleted', index);
  }

  getPatientName(index) {
    let pName = '';

    pName = (this.duplicatePatientArr[index][0].givenName && this.duplicatePatientArr[index][0].familyName) ? `${this.duplicatePatientArr[index][0].givenName} ${this.duplicatePatientArr[index][0].familyName}` : `NA`;

    return pName;
    
  }

  openModal(index) {
    this.tempararyPatientData = this.duplicatePatientArr[index];
    this.selectedPatientIndex = null;
  }

  selectedDuplicatePatient(index) {
    this.selectedPatientIndex = index;
  }

  mergePatient() {
    console.log('merge done');
  }
}
