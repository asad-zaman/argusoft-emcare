import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService, ToasterService } from 'src/app/shared';
@Component({
  selector: 'app-questionnaire-list',
  templateUrl: './questionnaire-list.component.html',
  styleUrls: ['./questionnaire-list.component.scss']
})
export class QuestionnaireListComponent implements OnInit {

  mainQuestionnaireList: any;
  filteredQuestionnaireList: any;
  searchString: string;
  currentPage = 0;
  totalCount = 0;
  tableSize = 10;
  error: any = null;
  isAPIBusy: boolean = true;
  isAdd: boolean = true;
  isEdit: boolean = true;
  isView: boolean = true;

  constructor(
    private readonly router: Router,
    private readonly fhirService: FhirService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.getQuestionnairesByPageIndex(this.currentPage);
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

  getQuestionnairesByPageIndex(index) {
    this.mainQuestionnaireList = [];
    this.fhirService.getQuestionnairesByPageIndex(index).subscribe(res => {
      if (res && res['list']) {
        this.mainQuestionnaireList = res['list'];
        this.filteredQuestionnaireList = this.mainQuestionnaireList;
        this.totalCount = res['totalCount'];
        this.isAPIBusy = false;
      }
    });
  }

  onIndexChange(event) {
    this.currentPage = event;
    this.getQuestionnairesByPageIndex(event - 1);
  }

  searchFilter() {
    const lowerCasedSearchString = this.searchString?.toLowerCase();
    this.filteredQuestionnaireList = this.mainQuestionnaireList.filter(questionnaire => {
      return (questionnaire.id?.toLowerCase().includes(lowerCasedSearchString)
        || questionnaire.title?.toLowerCase().includes(lowerCasedSearchString)
        || questionnaire.description?.toLowerCase().includes(lowerCasedSearchString))
    })
  }

  updateQuestionnaire(index) {
    this.router.navigate([`updateQuestionnaire/${this.filteredQuestionnaireList[index]['id']}`]);
  }

  resetPageIndex() {
    this.currentPage = 0;
  }

}
