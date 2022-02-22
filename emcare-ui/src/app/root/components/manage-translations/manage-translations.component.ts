import { Component, OnInit } from '@angular/core';
import { FhirService, ToasterService } from 'src/app/shared';
import * as _ from 'lodash';
import { LaunguageSubjects } from 'src/app/auth/token-interceptor';
// import frTrans from '../../../../assets/i18n/fr.json';
// import hinTrans from '../../../../assets/i18n/hin.json';
// import enTrans from '../../../../assets/i18n/en.json';

@Component({
  selector: 'app-manage-translations',
  templateUrl: './manage-translations.component.html',
  styleUrls: ['./manage-translations.component.scss']
})
export class ManageTranslationsComponent implements OnInit {

  frNames;
  hinNames;
  currentVal: string;
  lan: string;
  translationObject = {}; // this is the object which consists the data from backend
  currentNames = {};  //  this is the object which can be changed according to user configurations
  lanArray: Array<any> = [];
  alphabetArr = [];
  noRecords: boolean;
  isChanged: boolean;
  newSelectedLanguage;
  availableLanguages = [];

  constructor(
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly lanSubjects: LaunguageSubjects
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    //  for getting available Translations
    this.fhirService.getAllLaunguagesTranslations().subscribe(res => {
      if (res) {
        _.forIn(res, (value, _key) => {
          if (value.languageCode === 'fr') {
            this.frNames = JSON.parse(value.languageData);
            this.lanArray.push(value);
          } else if (value.languageCode === 'hin') {
            this.hinNames = JSON.parse(value.languageData);
            this.lanArray.push(value);
          }
        });
      }
    });
    this.setAlphabetArr();

    //  for getting all available Launguages 
    this.fhirService.getAllLaunguages().subscribe(res => {
      if (res) {
        res['languages'].map(lan => {
          this.availableLanguages.push({
            id: lan.language,
            lanName: lan.languageName,
            name: `${lan.languageName} => ${lan.nativeLanguageName}`
          });
        });
      }
    });

    // incase we want to update translation file in future
    // const data = {
    //   "id": value['id'],
    //   "languageCode": value['languageCode'],
    //   "languageName": value['languageName'],
    //   "languageTranslation": JSON.stringify(hinTrans)
    // }
    // this.fhirService.updateTranslation(data).subscribe(res => {
    //   this.toasterService.showSuccess('Translation changes saved successfully!', 'EMCARE');
    // });
  }

  setAlphabetArr() {
    let first = "A";
    let last = "Z";
    for (let i = first.charCodeAt(0); i <= last.charCodeAt(0); i++) {
      this.alphabetArr.push(eval("String.fromCharCode(" + i + ")"));
    }
  }

  // this will set the current inputs value
  setValue(event) {
    this.currentVal = event.target.value;
  }

  // this will set the current launguage
  setLaunguage(lanNumber) {
    if (lanNumber === 1) {
      this.lan = 'fr';
      this.setKeysForObject(this.frNames);
    } else {
      this.lan = 'hin';
      this.setKeysForObject(this.hinNames);
    }
  }

  setKeysForObject(object) {
    this.translationObject = {};
    this.currentNames = {};
    for (let key in object) {
      this.translationObject[key] = object[key];
      this.currentNames = this.translationObject;
    }
  }

  saveChange(key) {
    if (this.lan === 'fr') {
      this.frNames[`${key}`] = this.currentVal;
    } else if (this.lan === 'hin') {
      this.hinNames[`${key}`] = this.currentVal;
    }
    this.isChanged = true;
    this.toasterService.showSuccess('Translation saved successfully!', 'EMCARE');
  }

  saveTranslation() {
    let currTranslation;
    if (this.lan === 'fr') {
      currTranslation = this.frNames;
      this.lanSubjects.setLaunguage(this.lan);
      this.lanSubjects.setFrenchTranslations(currTranslation);
    } else if (this.lan === 'hin') {
      currTranslation = this.hinNames;
      this.lanSubjects.setLaunguage(this.lan);
      this.lanSubjects.setHindiTranslations(currTranslation);
    }

    const lanIndex = this.lanArray.findIndex(el => el.languageCode === this.lan);
    const data = {
      "id": this.lanArray[lanIndex]['id'],
      "languageCode": this.lanArray[lanIndex]['languageCode'],
      "languageName": this.lanArray[lanIndex]['languageName'],
      "languageTranslation": JSON.stringify(currTranslation)
    }
    this.fhirService.updateTranslation(data).subscribe(res => {
      this.toasterService.showSuccess('Translation changes saved successfully!', 'EMCARE');
    });
  }

  showCurrCharKeys(alpha) {
    this.currentNames = {};
    _.forIn(this.translationObject, (value, key) => {
      //  this will store the keys starting with the selected alphabets only
      if (key.startsWith(alpha)) {
        this.currentNames[key] = value;
      }
    });

    // if there are no keys for selected alphabets then show no records
    if (Object.keys(this.currentNames).length === 0) {
      this.noRecords = true;
    } else {
      this.noRecords = false;
    }
  }

  addNewLanguage() {
    const data = {
      "languageCode": this.newSelectedLanguage.id,
      "languageName": this.newSelectedLanguage.lanName
    }
    this.fhirService.addNewLaunguage(data).subscribe(res => {
      console.log(res);
    });
  }
}
