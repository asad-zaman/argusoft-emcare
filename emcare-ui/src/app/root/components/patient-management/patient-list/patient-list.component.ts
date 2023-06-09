import { Component, OnInit } from "@angular/core";
import { Subject } from "rxjs";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
import { AuthGuard } from "src/app/auth/auth.guard";
import { ToasterService } from "src/app/shared";
import { FhirService } from "src/app/shared/services/fhir.service";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
pdfMake.vfs = pdfFonts.pdfMake.vfs;
import { DatePipe } from '@angular/common';

import { Workbook } from 'exceljs';
import * as fs from 'file-saver';

@Component({
    selector: 'app-patient-list',
    templateUrl: './patient-list.component.html',
    styleUrls: ['./patient-list.component.scss']
})
export class PatientListComponent implements OnInit {

    patients: any
    filteredPatients: any;
    searchString: string;
    patientDetails: any
    showPatientDetailsFlag: boolean = false
    currentPage = 0;
    totalCount = 0;
    tableSize = 10;
    isAPIBusy: boolean = true;
    isLocationFilterOn: boolean = false;
    selectedId: any;
    searchTermChanged: Subject<string> = new Subject<string>();
    isView: boolean = true;
    showCheckboxes = false;
    enableAll = false;
    exportAllPatient = false;
    filteredAllPatientData = [];
    dateObj = {};
    columnOrder = ['resource_id', 'identifier', 'givenName',
        'familyName', 'gender', 'birthDate',
        'facilityName', 'addressLine', 'organizationName',
        'locationName', 'consultationDate'];

    constructor(
        private readonly fhirService: FhirService,
        private readonly toasterService: ToasterService,
        private readonly authGuard: AuthGuard,
        private datePipe: DatePipe
    ) { }

    ngOnInit(): void {
        this.prerequisite();
    }

    prerequisite() {
        this.checkFeatures();
        this.getPatientsByPageIndex(this.currentPage);
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
            this.patients = res['list'];
            this.filteredPatients = this.patients;
            this.totalCount = res['totalCount'];
            this.isAPIBusy = false;
            this.filteredPatients.forEach(element => {
                element['isExcelPDF'] = false;
            });
        }
    }

    getPatientsByPageIndex(index) {
        this.patients = [];
        this.fhirService.getPatientsByPageIndex(index).subscribe(res => {
            if (res) {
                this.manipulateResponse(res);
            }
        });
    }

    getPatientsBasedOnLocationAndPageIndex(pageIndex) {
        this.fhirService.getPatientsByLocationAndPageIndex(this.selectedId, pageIndex, this.dateObj).subscribe(res => {
            if (res) {
                this.manipulateResponse(res);
            }
        });
    }

    onIndexChange(event) {
        this.currentPage = event;
        if (this.isLocationFilterOn) {
            this.getPatientsBasedOnLocationAndPageIndex(event - 1);
        } else {
            if (this.searchString && this.searchString.length >= 1) {
                this.patients = [];
                this.fhirService.getPatientsByPageIndex(event - 1, this.searchString).subscribe(res => {
                    this.manipulateResponse(res);
                });
            } else {
                this.getPatientsByPageIndex(event - 1);
            }
        }
    }

    showPatientDetails(id) {
        this.fhirService.getPatientById(id).subscribe(res => {
            if (res) {
                this.patientDetails = res;
                this.showPatientDetailsFlag = true;
            }
        });
    }

    closePopup() {
        this.showPatientDetailsFlag = false;
        this.patientDetails = null;
    }

    searchFilter() {
        this.resetPageIndex();
        if (this.searchTermChanged.observers.length === 0) {
            this.searchTermChanged.pipe(
                debounceTime(1000),
                distinctUntilChanged()
            ).subscribe(_term => {
                if (this.exportAllPatient) {
                    this.exportAllPatient = !this.exportAllPatient;
                }
                if (this.searchString && this.searchString.length >= 1) {
                    this.patients = [];
                    this.fhirService.getPatientsByPageIndex(this.currentPage, this.searchString).subscribe(res => {
                        this.manipulateResponse(res);
                    });
                } else {
                    if (this.isLocationFilterOn) {
                        this.getPatientsBasedOnLocationAndPageIndex(this.currentPage);
                    } else {
                        this.getPatientsByPageIndex(this.currentPage);
                    }
                }
            });
        }
        this.searchTermChanged.next(this.searchString);
    }

    resetPageIndex() {
        this.currentPage = 0;
    }

    getLocationId(data) {
        if (this.exportAllPatient) {
            this.exportAllPatient = !this.exportAllPatient;
        }

        this.selectedId = data.locationId;
        this.dateObj = data.dateObj;

        if (this.selectedId && this.dateObj['startDate'] && this.dateObj['endDate']) {
            this.isLocationFilterOn = true;
        }
        this.resetPageIndex();
        const pageIndex = this.currentPage == 0 ? this.currentPage : this.currentPage - 1;
        this.getPatientsBasedOnLocationAndPageIndex(pageIndex);
    }

    clearFilter(event) {
        if (event) {
            this.resetPageIndex();
            this.getPatientsByPageIndex(this.currentPage);
        }
    }

    exportexcel(patient) {
        const data = [];
        const patientName = `${patient.givenName} ${patient.familyName}`;

        this.columnOrder.forEach(k => {
            if (k !== 'key') {
                if (k === 'dob' || k === 'consultationDate') {
                    data.push({ key: k, value: patient[k] ? this.datePipe.transform(patient[k], 'yyyy-MM-dd') : 'NA' });
                } else {
                    data.push({ key: k, value: patient[k] ? patient[k] : 'NA' });
                }
            }
        });
        let workbook = new Workbook();
        let worksheet = workbook.addWorksheet(patientName);

        worksheet.columns = [
            { header: 'Key', key: 'key', width: 20 },
            { header: 'Value', key: 'value', width: 35 },
        ];

        worksheet.addRows(data, "n");

        workbook.xlsx.writeBuffer().then((data) => {
            let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
            fs.saveAs(blob, `${patientName}.xlsx`);
        });
    }

    convertToExcel() {
        let selectedPatients;
        if (this.exportAllPatient) {
            selectedPatients = this.filteredAllPatientData['list'];
        } else {
            selectedPatients = this.filteredPatients.filter(el => el.isExcelPDF === true);
        }
        let workbook = new Workbook();
        let worksheet = workbook.addWorksheet(`Patient's Data`);

        const data = [];
        let columns = [{ header: 'Key', key: 'key', width: 20 }];
        selectedPatients.forEach((_patient, ind) => {
            // creating column 1 of patients
            let obj = { key: 'Patient' + parseInt(ind + 1) };
            data.push(obj);
        });

        this.columnOrder.forEach(k => {
            if (k !== 'key') {
                data.forEach((element, ind) => {
                    const patient = selectedPatients[ind];
                    if (k === 'dob' || k === 'consultationDate') {
                        element[k] = patient[k] ? this.datePipe.transform(patient[k], 'yyyy-MM-dd') : 'NA';
                    } else {
                        element[k] = patient[k] ? patient[k] : 'NA';
                    }
                });
                columns.push({ header: `${k}`, key: `${k}`, width: 35 })
            }
        });

        worksheet.columns = columns;
        worksheet.addRows(data, "n");

        workbook.xlsx.writeBuffer().then((data) => {
            let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
            fs.saveAs(blob, `PatientData.xlsx`);
        });
    }

    exportPDF(patient) {
        let data = [];
        let tableArr = [];
        data.push({ text: '                            ' });

        this.columnOrder.forEach(key => {
            if (key !== 'key') {
                if (key === 'dob' || key === 'consultationDate') {
                    tableArr.push([key, patient[key] ? this.datePipe.transform(patient[key], 'yyyy-MM-dd') : 'NA']);
                } else {
                    tableArr.push([key, patient[key] ? patient[key] : 'NA']);
                }
            }
        });

        let docDefinition = {
            content: [
                {
                    text: `${patient.givenName} ${patient.familyName}'s data`,
                    fontSize: 16,
                    color: '#047886'
                },
                {
                    columns: [data]
                },
                {
                    table: {
                        widths: ['auto', 'auto'],
                        body: tableArr
                    }
                }
            ]
        }

        pdfMake.createPdf(docDefinition).open();
        // pdfMake.createPdf(docDefinition).download(`${patient.givenName} ${patient.familyName}.pdf`);
    }

    convertToPDF() {
        let selectedPatients;
        if (this.exportAllPatient) {
            selectedPatients = this.filteredAllPatientData['list'];
        } else {
            selectedPatients = this.filteredPatients.filter(el => el.isExcelPDF === true);
        }
        let data = [];
        data.push({ text: '                            ' });

        let docDefinition: any = {
            content: [
                {
                    text: `Patients data`,
                    fontSize: 16,
                    alignment: 'center',
                    color: '#047886'
                },
                { columns: [{ text: '                            ' }] },
            ]
        }

        selectedPatients.forEach(patient => {
            let tableArr = [];
            this.columnOrder.forEach(key => {
                if (key !== 'key') {
                    if (key === 'dob' || key === 'consultationDate') {
                        tableArr.push([key, patient[key] ? this.datePipe.transform(patient[key], 'yyyy-MM-dd') : 'NA']);
                    } else {
                        tableArr.push([key, patient[key] ? patient[key] : 'NA']);
                    }
                }
            });

            let tableObj = {};
            tableObj = {
                widths: ['auto', 'auto'],
                body: tableArr
            }

            docDefinition.content.push({ columns: [{ text: '                            ' }] })
            docDefinition.content.push({ text: `${patient.givenName} ${patient.familyName}'s data`, fontSize: 16, color: '#047886' })
            docDefinition.content.push({ columns: [{ text: '                            ' }] })
            docDefinition.content.push({ table: tableObj })
        });

        pdfMake.createPdf(docDefinition).open();
    }

    onEnableSelectionClick() {
        this.showCheckboxes = !this.showCheckboxes;
        this.enableAll = false;
        if (!this.showCheckboxes) {
            this.filteredPatients.forEach(element => { element['isExcelPDF'] = false; });
        }
    }

    enableAllBoxes() {
        if (this.enableAll) {
            this.filteredPatients.forEach(element => { element['isExcelPDF'] = true; });
        } else {
            this.filteredPatients.forEach(element => { element['isExcelPDF'] = false; });
        }
    }

    enableEachBox(patient) {
        if (!patient.isExcelPDF) {
            this.enableAll = false;
        } else {
            const checkLength = this.filteredPatients.filter(element => element['isExcelPDF'] === true).length;
            if (this.filteredPatients.length === checkLength) {
                this.enableAll = true;
            }
        }
    }

    exportAllThePatients() {
        this.enableAll = false;
        this.showCheckboxes = false;
        this.fhirService.getAllPatientsForExport(this.searchString, this.selectedId).subscribe((res: any) => {
            if (res) {
                this.filteredAllPatientData = res;
            }
        });
    }
}
