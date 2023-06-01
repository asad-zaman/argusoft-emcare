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
import { Workbook } from 'exceljs';
import * as fs from 'file-saver';

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
  showCheckboxes = false;
  enableAll = false;
  exportAllConsultations = false;
  filteredAllConsultations = [];
  btnValue = 'Export';

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
      this.filteredConsultations.forEach(element => {
        element['isExcelPDF'] = false;
      });
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
        if (this.exportAllConsultations) {
          this.exportAllConsultations = !this.exportAllConsultations;
        }
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

  onEnableSelectionClick() {
    this.showCheckboxes = !this.showCheckboxes;
    this.enableAll = false;
    if(this.btnValue == 'Export') {
      this.btnValue = 'Cancel';
    } else {
      this.btnValue= 'Export';
    }
    if (!this.showCheckboxes) {
      this.filteredConsultations.forEach(element => { element['isExcelPDF'] = false; });
    }
  }

  enableAllBoxes() {
    if (this.enableAll) {
      this.filteredConsultations.forEach(element => { element['isExcelPDF'] = true; });
    } else {
      this.filteredConsultations.forEach(element => { element['isExcelPDF'] = false; });
    }
  }

  enableEachBox(patient) {
    if (!patient.isExcelPDF) {
      this.enableAll = false;
    } else {
      const checkLength = this.filteredConsultations.filter(element => element['isExcelPDF'] === true).length;
      if (this.filteredConsultations.length === checkLength) {
        this.enableAll = true;
      }
    }
  }

  exportexcel(patient) {
    const patientName = `${patient.givenName} ${patient.familyName}`;
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet(patientName);

    worksheet.columns = [
      { header: 'Consultation', key: 'Consultation', width: 25 },
      { header: 'linkId', key: 'linkId', width: 20 },
      { header: 'text', key: 'text', width: 35 },
      { header: 'answer', key: 'answer', width: 35 },
    ];

    let answerData: any = [];
    this.fhirService.getConsultationExportData(patient.id).subscribe((res: any) => {
      answerData.push({
        Consultation: `${patient.givenName} ${patient.familyName}'s consultation data`,
        linkId: '-', text: '-', answer: '-'
      });
      res.forEach((el, ind) => {
        if (el) {
          const currData = JSON.parse(el);
          for (const key in currData) {
            if (key == 'item') {
              const items = currData[key];
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
                  answerData.push(answerObjForArr);
                }
              });
            }
          }
        }
      });

      worksheet.addRows(answerData, "n");
      workbook.xlsx.writeBuffer().then((data) => {
        let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        fs.saveAs(blob, `${patientName}.xlsx`);
      });
    });
  }

  convertToExcel() {
    let selectedConsultations;
    let answerData: any = [];
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('Consultation Data');
    worksheet.columns = [
      { header: 'Consultation', key: 'Consultation', width: 25 },
      { header: 'linkId', key: 'linkId', width: 20 },
      { header: 'text', key: 'text', width: 35 },
      { header: 'answer', key: 'answer', width: 35 },
    ];

    if (this.exportAllConsultations) {
      let count = 0;
      for (const key in this.filteredAllConsultations) {
        count++;
        const element = this.filteredAllConsultations[key];
        if (count >= 1) {
          answerData.push({ Consultation: ``, linkId: '', text: '', answer: '' });
        }
        answerData.push({
          Consultation: `${key}'s consultation data`,
          linkId: '-', text: '-', answer: '-'
        });
        answerData = this.manipulateData(element, answerData);
      }
    } else {
      selectedConsultations = this.filteredConsultations.filter(el => el.isExcelPDF === true);
      selectedConsultations.forEach((patient, i) => {
        this.fhirService.getConsultationExportData(patient.id).subscribe((res: any) => {
          if (i >= 1) {
            answerData.push({ Consultation: ``, linkId: '', text: '', answer: '' });
          }
          answerData.push({
            Consultation: `${patient.givenName} ${patient.familyName}'s consultation data`,
            linkId: '-', text: '-', answer: '-'
          });
          answerData = this.manipulateData(res, answerData);
        });
      });
    }

    setTimeout(() => {
      worksheet.addRows(answerData, "n");
      workbook.xlsx.writeBuffer().then((data) => {
        let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        fs.saveAs(blob, `ConsultationData.xlsx`);
      });
    }, 1000);
  }

  manipulateData(res, answerData) {
    res.forEach((el, ind) => {
      if (el) {
        const currData = JSON.parse(el);
        for (const key in currData) {
          if (key == 'item') {
            const items = currData[key];
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
                answerData.push(answerObjForArr);
              }
            });
          }
        }
      }
    });
    return answerData;
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
    });
  }

  convertToPDF() {
    let selectedConsultations;
    let data = [];
    data.push({ text: '                            ' });

    let contentArr = [];
    contentArr.push({
      text: `Consultation's data`,
      fontSize: 16,
      alignment: 'center',
      color: '#047886'
    });
    contentArr.push({ columns: [{ text: '                            ' }] });

    if (this.exportAllConsultations) {
      for (const key in this.filteredAllConsultations) {
        const element = this.filteredAllConsultations[key];
        let answerInnerData = [];
        let answerOuterData = [];
        let data = [];

        let tempObj = this.manipulatePDFData(element, data, answerInnerData, answerOuterData);
        data = tempObj.data;
        answerInnerData = tempObj.answerInnerData;
        answerOuterData = tempObj.answerOuterData;

        contentArr.push({
          text: `${key}'s consultation data`,
          alignment: 'center', fontSize: 16, color: 'red'
        });
        contentArr.push({ columns: [{ text: '                            ' }] })
        contentArr.push({ columns: [{ text: '                            ' }] })

        data.forEach((element, index) => {
          let tableObj = {};
          tableObj = {
            widths: ['auto', 'auto', 'auto'],
            body: [
              ['Link Id', 'Text', 'Answer'],
              ...answerOuterData[index].map(p => ([p.linkId, p.text, p.answer]))
            ]
          }
          contentArr.push({ columns: [element], color: '#047886' })
          contentArr.push({ columns: [{ text: '                            ' }] })
          contentArr.push({ table: tableObj })
          contentArr.push({ columns: [{ text: '                            ' }] })
        });
      }
    } else {
      selectedConsultations = this.filteredConsultations.filter(el => el.isExcelPDF === true);
      selectedConsultations.forEach(consultation => {
        this.fhirService.getConsultationExportData(consultation.id).subscribe((res: any) => {
          let answerInnerData = [];
          let answerOuterData = [];
          let data = [];

          let tempObj = this.manipulatePDFData(res, data, answerInnerData, answerOuterData);
          data = tempObj.data;
          answerInnerData = tempObj.answerInnerData;
          answerOuterData = tempObj.answerOuterData;

          contentArr.push({
            text: `${consultation.givenName} ${consultation.familyName}'s consultation data`,
            alignment: 'center', fontSize: 16, color: 'red'
          });
          contentArr.push({ columns: [{ text: '                            ' }] })
          contentArr.push({ columns: [{ text: '                            ' }] })

          data.forEach((element, index) => {
            let tableObj = {};
            tableObj = {
              widths: ['auto', 'auto', 'auto'],
              body: [
                ['Link Id', 'Text', 'Answer'],
                ...answerOuterData[index].map(p => ([p.linkId, p.text, p.answer]))
              ]
            }
            contentArr.push({ columns: [element], color: '#047886' })
            contentArr.push({ columns: [{ text: '                            ' }] })
            contentArr.push({ table: tableObj })
            contentArr.push({ columns: [{ text: '                            ' }] })
          });
        });
      });
    }

    setTimeout(() => {
      let docDefinition = {
        content: contentArr
      }
      pdfMake.createPdf(docDefinition).open();
    }, 500);
  }

  manipulatePDFData(res, data, answerInnerData, answerOuterData) {
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
    return { data, answerInnerData, answerOuterData };
  }

  exportAllTheConsultations() {
    this.enableAll = false;
    this.showCheckboxes = false;
    if (this.exportAllConsultations) {
      this.fhirService.getAllConsultationsForExport().subscribe((res: any) => {
        if (res) {
          this.filteredAllConsultations = res;
          console.log(this.filteredAllConsultations);
        }
      });
    }
  }
}

