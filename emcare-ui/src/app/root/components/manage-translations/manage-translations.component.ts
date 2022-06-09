import { Component, OnInit } from '@angular/core';
import { FhirService, ToasterService } from 'src/app/shared';
import * as _ from 'lodash';
import { LaunguageSubjects } from 'src/app/auth/token-interceptor';
import { forkJoin } from 'rxjs';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { ActivatedRoute } from '@angular/router';
// import enTrans from '../../../../assets/i18n/en.json';
@Component({
  selector: 'app-manage-translations',
  templateUrl: './manage-translations.component.html',
  styleUrls: ['./manage-translations.component.scss']
})
export class ManageTranslationsComponent implements OnInit {

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
  editLanCode: string;
  isAddFeature: boolean;
  isEditFeature: boolean;
  isAllowed: boolean = true;
  isEdit: boolean;
  isTranslationSaved: boolean = true;

  constructor(
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly lanSubjects: LaunguageSubjects,
    private readonly authGuard: AuthGuard,
    private readonly route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkEditParams();
    this.checkFeatures();
    forkJoin([
      this.fhirService.getAllLaunguagesTranslations(),
      this.fhirService.getAllLaunguages()
    ]).subscribe(result => {
      if (result && result.length > 0) {
        this.manipulateFirstResult(result[0]);
        this.manipulateSecondResult(result[1]);
      }
    }, () => {
      this.toasterService.showToast('error', 'API issue!', 'EMCARE');
    });

    this.setAlphabetArr();

    // incase we want to update translation file in future
    // const data = {
    //   "id": 201,
    //   "languageCode": 'en',
    //   "languageName": 'English',
    //   "languageTranslation": JSON.stringify(enTrans)
    // }
    // this.fhirService.updateTranslation(data).subscribe(res => {
    //   this.toasterService.showToast('success', 'New Translations added successfully!', 'EMCARE');
    // });
  }

  checkEditParams() {
    const routeParams = this.route.snapshot.paramMap;
    this.editLanCode = routeParams.get('code');
    if (this.editLanCode) {
      this.isEdit = true;
    }
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAddFeature = res.featureJSON['canAdd'];
        this.isEditFeature = res.featureJSON['canEdit'];
        if (this.isAddFeature && this.isEditFeature) {
          this.isAllowed = true;
        } else if (this.isAddFeature && !this.isEdit) {
          this.isAllowed = true;
        } else if (!this.isEditFeature && this.isEdit) {
          this.isAllowed = false;
        } else if (!this.isAddFeature && this.isEdit) {
          this.isAllowed = true;
        } else if (this.isEditFeature && this.isEdit) {
          this.isAllowed = true;
        } else {
          this.isAllowed = false;
        }
      }
    });
  }

  manipulateFirstResult(res) {
    if (res) {
      _.forIn(res, (value, _key) => {
        this.lanArray.push(value);
      });
      if (this.editLanCode) {
        const lan = this.lanArray.find(l => l.languageCode === this.editLanCode);
        this.setLaunguage(lan);
      }
    }
  }

  manipulateSecondResult(res) {
    if (res) {
      const availArr = _.differenceBy(res['languages'], this.lanArray, 'languageName');
      availArr.map(lan => {
        this.availableLanguages.push({
          id: lan.language,
          lanName: lan.languageName,
          name: `${lan.languageName} => ${lan.nativeLanguageName}`
        });
      });
    }
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
  setLaunguage(lan) {
    this.lan = lan.languageCode;
    this.setKeysForObject(JSON.parse(lan.languageData));
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
    if (this.currentVal) {
      this.translationObject[`${key}`] = this.currentVal;
      this.isChanged = true;
      this.toasterService.showToast('success', 'Translation saved successfully!', 'EMCARE');
      this.isTranslationSaved = false;
    } else {
      this.toasterService.showToast('info', 'Please provide some translation!', 'EMCARE');
    }
  }

  saveTranslation() {
    const lanIndex = this.lanArray.findIndex(el => el.languageCode === this.lan);
    const data = {
      "id": this.lanArray[lanIndex]['id'],
      "languageCode": this.lanArray[lanIndex]['languageCode'],
      "languageName": this.lanArray[lanIndex]['languageName'],
      "languageTranslation": JSON.stringify(this.translationObject)
    }
    this.fhirService.updateTranslation(data).subscribe(res => {
      if (localStorage.getItem('language') === this.lanArray[lanIndex]['languageCode']) {
        this.lanSubjects.setLaunguage(this.lanArray[lanIndex]['languageCode']);
        this.lanSubjects.setCurrentTranslation(JSON.stringify(this.translationObject));
      }
      this.toasterService.showToast('success', 'Translation changes saved successfully!', 'EMCARE');
    });
    this.isTranslationSaved = true;
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
      if (res) {
        this.lanArray.push(res);
        this.availableLanguages = this.availableLanguages.filter(el => el.id !== this.newSelectedLanguage.id);
        this.newSelectedLanguage = null;
        this.toasterService.showToast('success', 'Launguage added successfully!', 'EMCARE');
      }
    }, (_error) => {
      this.toasterService.showToast('error', 'Launguage not added successfully!', 'EMCARE');
    });
  }
}
