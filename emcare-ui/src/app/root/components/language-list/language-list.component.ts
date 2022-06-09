import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';
import * as _ from 'lodash';

@Component({
  selector: 'app-language-list',
  templateUrl: './language-list.component.html',
  styleUrls: ['./language-list.component.scss']
})
export class LanguageListComponent implements OnInit {

  isAPIBusy: boolean = true;
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;
  lanArray = [];

  constructor(
    private readonly router: Router,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard,
    private readonly fhirService: FhirService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getAllLanguages();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAdd = res.featureJSON['canAdd'];
        this.isEdit = res.featureJSON['canEdit'];
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  getAllLanguages() {
    this.fhirService.getAllLaunguagesTranslations().subscribe(res => {
      this.isAPIBusy = false;
      if (res) {
        _.forIn(res, (value, _key) => {
          this.lanArray.push(value);
        });
      }
    }, () => {
      this.toasterService.showToast('error', 'API issue!', 'EMCARE');
    })
  }

  updateLanguage(index) {
    this.router.navigate([`manage-language/${this.lanArray[index]['languageCode']}`]);
  }
}
