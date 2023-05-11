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
      let answerInnerData = [];
      let answerOuterData = [];
      let data = [];
      res.forEach((el, ind) => {
        if (el) {
          const currData = JSON.parse(el);
          let str = `${ind + 1}) ` + currData['resourceType'] + ' => ' + currData['questionnaire'];
          let obj = { text: str };
          data.push(obj);
          for (const key in currData) {
            if (key == 'item') {
              const items = currData[key];
              answerInnerData = [];
              items.forEach(item => {
                const answerObjForArr = {};
                if (item.hasOwnProperty('text') && item.hasOwnProperty('answer')) {
                  for (const k in item) {
                    if (k === 'answer') {
                      const answerObj = item['answer'][0];
                      for (const a in answerObj) {
                        if (a !== 'item' && a !== 'valueCoding' && a !== 'valueQuantity') {
                          answerObjForArr[k] = answerObj[a];
                        } else {
                          if (a !== 'item') {
                            const val = answerObj[a].display ? answerObj[a].display : answerObj[a].value;
                            answerObjForArr[k] = val;
                          }
                        }
                      }
                    } else {
                      answerObjForArr[k] = item[k];
                    }
                  }
                  answerInnerData.push(answerObjForArr);
                }
              });
              answerOuterData.push(answerInnerData);
            }
          }
        }
      });

      let docDefinition: any = {
        content: [
          {
            text: `${patient.givenName} ${patient.familyName}'s consultation data`,
            fontSize: 16,
            alignment: 'center',
            color: '#047886'
          },
          { columns: [{ text: '                            ' }] },
          {
            text: 'Consultation Details',
            fontSize: 14,
            alignment: 'center',
            color: '#047886',
            style: 'sectionHeader'
          }
        ]
      }
      docDefinition.content.push({ columns: [{ text: '                            ' }] })

      data.forEach((element, index) => {
        let tableObj = {};
        tableObj = {
          widths: ['auto', 'auto', 'auto'],
          body: [
            ['Link Id', 'Text', 'Answer'],
            ...answerOuterData[index].map(p => ([p.linkId, p.text, p.answer]))
          ]
        }
        docDefinition.content.push({ columns: [element], color: '#047886' })
        docDefinition.content.push({ columns: [{ text: '                            ' }] })
        docDefinition.content.push({ table: tableObj })
        docDefinition.content.push({ columns: [{ text: '                            ' }] })
      });

      pdfMake.createPdf(docDefinition).open();
      // pdfMake.createPdf(docDefinition).download(`${patient.givenName} ${patient.familyName}.pdf`);
    });
  }
}

