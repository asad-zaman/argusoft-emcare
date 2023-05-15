import { Component, OnInit } from "@angular/core";
import { Subject } from "rxjs";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
import { AuthGuard } from "src/app/auth/auth.guard";
import { ToasterService } from "src/app/shared";
import { FhirService } from "src/app/shared/services/fhir.service";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";
pdfMake.vfs = pdfFonts.pdfMake.vfs;

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

    constructor(
        private readonly fhirService: FhirService,
        private readonly toasterService: ToasterService,
        private readonly authGuard: AuthGuard
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
                element['isExcel'] = false;
            });
        }
    }

    getPatientsByPageIndex(index) {
        this.patients = [];
        this.fhirService.getPatientsByPageIndex(index).subscribe(res => {
            this.manipulateResponse(res);
        });
    }

    getPatientsBasedOnLocationAndPageIndex(pageIndex) {
        this.fhirService.getPatientsByLocationAndPageIndex(this.selectedId, pageIndex).subscribe(res => {
            if (res) {
                this.filteredPatients = [];
                this.filteredPatients = res['list'];
                this.totalCount = res['totalCount'];
                this.isAPIBusy = false;
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
        this.selectedId = data;
        if (this.selectedId) {
            this.isLocationFilterOn = true;
            this.resetPageIndex();
            const pageIndex = this.currentPage == 0 ? this.currentPage : this.currentPage - 1;
            this.getPatientsBasedOnLocationAndPageIndex(pageIndex);
        } else {
            this.toasterService.showToast('info', 'Please select Location!', 'EMCARE');
        }
    }

    clearFilter(event) {
        if (event) {
            this.resetPageIndex();
            this.getPatientsByPageIndex(this.currentPage);
        }
    }

    exportPDF(patient) {
        let data = [];
        let tableArr = [];
        data.push({ text: '                            ' });

        for (const key in patient) {
            tableArr.push([key, patient[key] ? patient[key] : 'NA']);
        }

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

    exportexcel(patient) {
        const data = [];
        const patientName = `${patient.givenName} ${patient.familyName}`;

        for (const k in patient) {
            data.push({ key: k, value: patient[k] ? patient[k] : 'NA' });
        }

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
        const selectedPatients = this.filteredPatients.filter(el => el.isExcel === true);
        let workbook = new Workbook();
        selectedPatients.forEach((patient, ind) => {
            const data = [];
            const patientName = `Paitent-${ind + 1}`;
            for (const k in patient) {
                data.push({ key: k, value: patient[k] ? patient[k] : 'NA' });
            }
            let worksheet = workbook.addWorksheet(patientName);
            worksheet.columns = [
                { header: 'Key', key: 'key', width: 20 },
                { header: 'Value', key: 'value', width: 35 },
            ];
            worksheet.addRows(data, "n");
        });

        workbook.xlsx.writeBuffer().then((data) => {
            let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
            fs.saveAs(blob, `PatientData.xlsx`);
        });
    }

    onEnableSelectionClick() {
        this.showCheckboxes = !this.showCheckboxes; 
        if (!this.showCheckboxes) {
            this.filteredPatients.forEach(element => { element['isExcel'] = false; });
        }
    }

    enableAllBoxes() {
        if (this.enableAll) {
            this.filteredPatients.forEach(element => { element['isExcel'] = true; });
        } else {
            this.filteredPatients.forEach(element => { element['isExcel'] = false; });
        }
    }

    enableEachBox(patient) {
        if (!patient.isExcel) {
            this.enableAll = false;
        } else {
            const checkLength = this.filteredPatients.filter(element => element['isExcel'] === true).length;
            if (this.filteredPatients.length === checkLength) {
                this.enableAll = true;
            }
        }
    }
}