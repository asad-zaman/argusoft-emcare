import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { Subject } from "rxjs";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
import { AuthGuard } from "src/app/auth/auth.guard";
import { FhirService } from "src/app/shared/services/fhir.service";
import * as _ from 'lodash';
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
pdfMake.vfs = pdfFonts.pdfMake.vfs;

@Component({
  selector: 'app-consultation-list',
  templateUrl: './consultation-list.component.html',
  styleUrls: ['./consultation-list.component.scss']
})
export class ConsultationListComponent implements OnInit {

  consultations: any
  filteredConsultations: any;
  searchString: string;
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  isAPIBusy: boolean = true;
  searchTermChanged: Subject<string> = new Subject<string>();
  isView: boolean = true;

  constructor(
    private readonly fhirService: FhirService,
    private readonly authGuard: AuthGuard,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getConsultationsByPageIndex(this.currentPage);
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  manipulateResponse(res) {
    if (res && res['list']) {
      this.consultations = res['list'];
      this.filteredConsultations = this.consultations;
      this.filteredConsultations = _.sortBy(this.filteredConsultations, 'consultationDate').reverse();
      this.totalCount = res['totalCount'];
      this.isAPIBusy = false;
    }
  }

  getConsultationsByPageIndex(index) {
    this.consultations = [];
    this.fhirService.getConsultationList(index).subscribe(res => {
      this.manipulateResponse(res);
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    if (this.searchString && this.searchString.length >= 1) {
      this.consultations = [];
      this.fhirService.getPatientsByPageIndex(event - 1, this.searchString).subscribe(res => {
        this.manipulateResponse(res);
      });
    } else {
      this.getConsultationsByPageIndex(event - 1);
    }
  }

  searchFilter() {
    this.resetPageIndex();
    if (this.searchTermChanged.observers.length === 0) {
      this.searchTermChanged.pipe(
        debounceTime(1000),
        distinctUntilChanged()
      ).subscribe(_term => {
        if (this.searchString && this.searchString.length >= 1) {
          this.consultations = [];
          this.fhirService.getPatientsByPageIndex(this.currentPage, this.searchString).subscribe(res => {
            this.manipulateResponse(res);
          });
        } else {
          this.getConsultationsByPageIndex(this.currentPage);
        }
      });
    }
    this.searchTermChanged.next(this.searchString);
  }

  resetPageIndex() {
    this.currentPage = 0;
  }

  viewConsultation(index) {
    this.router.navigate([`view-consultation/${this.filteredConsultations[index]['id']}`]);
  }

  exportPDF(patient) {
    this.fhirService.getConsultationExportData(patient.id).subscribe((res: any) => {
      let data = [];
      data.push({ text: '                            ' });
      res.forEach(el => {
        if (el) {
          const currData = JSON.parse(el);
          for (const key in currData) {
            if (key == 'subject' || key == 'encounter') {
              const innerObj = currData[key];
              data.push({ text: key })
              for (const k in innerObj) {
                let str = k + ' =>   ' + JSON.stringify(innerObj[k])
                let obj = { text: str };
                data.push(obj);
              }
              data.push({ text: '                            ' });
            } else if (key == 'item') {
              const items = currData[key];
              console.log(items);
              data.push({ text: key })
              items.forEach(item => {
                for (const k in item) {
                  let str = k + ' =>   ' + JSON.stringify(item[k])
                  let obj = { text: str };
                  data.push(obj);
                }
                data.push({ text: '                            ' });
              });
            } else {
              let str = key + ' =>   ' + JSON.stringify(currData[key])
              let obj = { text: str };
              data.push(obj);
              data.push({ text: '                            ' });
            }
          }
          data.push({ text: '----------------------------------------------------------------------------------------' });
          data.push({ text: '                            ' });
        }
      });

      let docDefinition = {
        content: [
          {
            text: `${patient.givenName} ${patient.familyName}'s consultation data`,
            fontSize: 16,
            alignment: 'center',
            color: '#047886'
          },
          {
            columns: [data]
          }
        ]
      }
      pdfMake.createPdf(docDefinition).open();
      // pdfMake.createPdf(docDefinition).download(`${patient.givenName} ${patient.familyName}.pdf`);
    });
  }
}

