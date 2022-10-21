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
    0: 'REGISTRATION_PATIENT', 1: 'DANGER_SIGNS', 2: 'MEASUREMENTS',
    3: 'SYMPTOMS', 4: 'SIGNS', 5: 'ASSESSMENTS', 6: 'CLASSIFICATIONS'
  }

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
  }

  manipulateResAsPerKey(tempkeyData) {
    let tempDataArr = [];
    tempkeyData.forEach(el => {
      if (el.consultationStage !== 'REGISTRATION_ENCOUNTER') {
        tempDataArr.push({
          stage: el.consultationStage,
          resText: JSON.parse(el.questionnaireResponseText)
        });
      }
    });
    tempDataArr.forEach(el => {
      let queAnsObj = [];
      el.resText.item.forEach(itemEl => {
        if (itemEl.text && itemEl.answer) {
          if (itemEl.answer[0].valueCoding && itemEl.answer[0].valueCoding.display) {
            queAnsObj.push({
              label: itemEl.text,
              value: itemEl.answer[0].valueCoding.display
            });
          } else if (itemEl.answer[0].valueBoolean && itemEl.answer[0].valueBoolean) {
            queAnsObj.push({
              label: itemEl.text,
              value: itemEl.answer[0].valueBoolean
            });
          } else if (itemEl.answer[0].valueString && itemEl.answer[0].valueString) {
            queAnsObj.push({
              label: itemEl.text,
              value: itemEl.answer[0].valueString
            });
          } else if (itemEl.answer[0].valueDate && itemEl.answer[0].valueDate) {
            queAnsObj.push({
              label: itemEl.text,
              value: itemEl.answer[0].valueDate
            });
          } else if (itemEl.answer[0].valueQuantity && itemEl.answer[0].valueQuantity.value) {
            queAnsObj.push({
              label: itemEl.text,
              value: itemEl.answer[0].valueQuantity.value
            });
          } else if (itemEl.answer[0].valueInteger && itemEl.answer[0].valueInteger) {
            queAnsObj.push({
              label: itemEl.text,
              value: itemEl.answer[0].valueInteger
            });
          } else { }
        }
      });
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
}
