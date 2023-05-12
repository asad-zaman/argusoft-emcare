import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService } from 'src/app/shared';

@Component({
  selector: 'app-view-consultation',
  templateUrl: './view-consultation.component.html',
  styleUrls: ['./view-consultation.component.scss']
})
export class ViewConsultationComponent implements OnInit {

  isView: boolean = true;
  editId: string;
  isEdit: boolean = false;
  encounterKeys = [];
  selectedEnKey = {};
  selectedEncounterIcon = 0;
  data = [];
  currentObj = {};
  iconObj = {
    0: 'Registration', 1: 'Danger Signs', 2: 'Measurements', 3: 'Symptoms',
    4: 'Signs', 5: 'Assessments', 6: 'Classifications',
  }
  iconObjRes = {
    0: 'REGISTRATION_ENCOUNTER', 1: 'DANGER_SIGNS', 2: 'MEASUREMENTS',
    3: 'SYMPTOMS', 4: 'SIGNS', 5: 'ASSESSMENTS', 6: 'CLASSIFICATIONS'
  }
  patientData = {};

  constructor(
    private readonly fhirService: FhirService,
    private readonly authGuard: AuthGuard,
    private readonly route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkEditParam();
    this.checkFeatures();
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.getPatientDetails();
      this.getPatientEncounter();
    }
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  getPatientEncounter() {
    this.fhirService.getPatientEncounter(this.editId).subscribe(res => {
      if (res) {
        this.manipulateRes(res);
      }
    });
  }

  manipulateRes(res) {
    for (let key in res) {
      if (res.hasOwnProperty(key)) {
        this.encounterKeys.push({ name: key, data: res[key] });
      }
    }
  }

  getEncounterData() {
    const tempData = this.encounterKeys.find(el => el.id = this.selectedEnKey).data;
    this.manipulateResAsPerKey(tempData);
  }

  selectEncounter(index) {
    this.selectedEncounterIcon = index;
    this.currentObj = this.data[this.selectedEncounterIcon];
    console.log(this.currentObj);
  }

  manipulateResAsPerKey(tempkeyData) {
    let tempDataArr = [];
    tempkeyData.forEach(el => {
      tempDataArr.push({
        stage: el.consultationStage,
        resText: el.questionnaireResponseText !== '' ? JSON.parse(el.questionnaireResponseText) : null
      });
    });
    tempDataArr.forEach(el => {
      let queAnsObj = [];
      if (el.resText) {
        el.resText.item.forEach(itemEl => {
          if (itemEl.text && itemEl.answer) {
            for (const k in itemEl) {
              if (k === 'answer') {
                const answerObj = itemEl['answer'][0];
                for (const a in answerObj) {
                  if (a !== 'item' && a !== 'valueCoding' && a !== 'valueQuantity') {
                    queAnsObj.push({ label: itemEl.text, value: answerObj[a] });
                  } else {
                    if (a !== 'item') {
                      const val = answerObj[a].display ? answerObj[a].display : answerObj[a].value;
                      queAnsObj.push({ label: itemEl.text, value: val });
                    }
                  }
                }
              }
            }
          }
        });
      }
      el['queAnsObj'] = queAnsObj;
    });
    for (let key in this.iconObjRes) {
      if (this.iconObjRes.hasOwnProperty(key)) {
        const item = tempDataArr.find(el => el.stage === this.iconObjRes[key]);
        this.data.push(item);
      }
    }
    this.currentObj = this.data[0];
  }

  getPatientDetails() {
    this.fhirService.getPatientById(this.editId).subscribe(res => {
      if (res) {
        this.patientData['fLetter'] = res['givenName'] ? res['givenName'].substr(0, 1) : null;
        this.patientData['name'] = res['givenName'] || res['familyName'] ? `${res['givenName']} ${res['familyName']}` : 'NA';
        this.patientData['gender'] = res['gender'];
        let diff: number = (new Date().getTime()) - res['dob'];
        //millisecond in a day
        let msDay: number = 24 * 60 * 60 * 1000;
        //ml per year
        let msYear: number = msDay * 365;
        let ageYear: number = Math.floor(diff / msYear);
        let ageMonth: number = Math.floor(diff % msYear / (msDay * 30));
        let ageDay: number = Math.floor(((diff % msYear) % (msDay * 30)) / msDay);
        let year: string = ageYear == 1 ? "Year" : "Years";
        let month: string = ageMonth < 2 ? "Month" : "Months";
        let day: string = ageDay < 2 ? "Day" : "Days";
        this.patientData['age'] = ageYear === 0 && ageMonth === 0 ? ageDay.toString() + " " + day : ageYear === 0 ? ageMonth.toString() + " " + month + " " + ageDay.toString() + " " + day : ageYear.toString() + " " + year + " " + ageMonth.toString() + " " + month + " " + ageDay.toString() + " " + day;
      }
    });
  }
}
