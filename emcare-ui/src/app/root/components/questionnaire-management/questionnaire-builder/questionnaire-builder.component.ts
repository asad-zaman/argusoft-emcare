import { Component, HostListener, OnInit } from "@angular/core";
import { DomSanitizer } from "@angular/platform-browser";
import { ActivatedRoute } from "@angular/router";
import { AuthGuard } from "src/app/auth/auth.guard";
import { FhirService, ToasterService } from "src/app/shared";
import { environment } from 'src/environments/environment';
@Component({
    selector: 'app-questionnaire-builder',
    templateUrl: './questionnaire-builder.component.html',
    styleUrls: ['./questionnaire-builder.component.scss']
})
export class QuestionnaireBuilderComponent implements OnInit {

    isEdit: boolean = false;
    questionnaireId: string = null;
    frameLoaded: boolean = false;
    questionnaireBuilderUrl = null;
    questionnaireBuilderEditUrl = null;
    isAddFeature: boolean = true;
    isEditFeature: boolean = true;
    isAllowed: boolean = true;

    constructor(
        private readonly sanitizer: DomSanitizer,
        private readonly route: ActivatedRoute,
        private readonly fhirService: FhirService,
        private readonly toasterService: ToasterService,
        private readonly authGuard: AuthGuard
    ) { };

    ngOnInit(): void {
        this.questionnaireBuilderUrl = this.sanitizer.bypassSecurityTrustResourceUrl(environment.questionnaireBuilderUrl);
        this.questionnaireBuilderEditUrl = this.sanitizer.bypassSecurityTrustResourceUrl(environment.questionnaireBuilderUrl + '/?isEdit=true');
        this.prerequisite();
    }

    //For Toasters on iframe api calls.
    @HostListener('window:message', ['$event']) onPostMessage(e) {
        if (e.data?.apiMessage == 'save') {
            this.toasterService.showToast('success', 'Questionnaire Saved Successfully!', 'EMCARE');
        }
        else if (e.data?.apiMessage == 'failure') {
            this.toasterService.showToast('error', 'Error encountered while saving questionnaire!', 'EMCARE');
        }
    }

    prerequisite() {
        this.checkFeatures();
        const routeParams = this.route.snapshot.paramMap;
        this.questionnaireId = routeParams.get('id');
        if (this.questionnaireId != null) {
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

    sendQuestionnaire() {
        let iframe = document.getElementById('iframe');
        let iWindow = (<HTMLIFrameElement>iframe).contentWindow;
        this.fhirService.getQuestionnaireResourceById(this.questionnaireId).subscribe(res => {
            iWindow.postMessage({ questionnaireBody: JSON.stringify(res) }, '*');
        })
    }


    sendAuthToken() {
        let iframe = document.getElementById('iframe');
        let iWindow = (<HTMLIFrameElement>iframe).contentWindow;
        let accessToken = localStorage.getItem('access_token');
        iWindow.postMessage({ accessToken: accessToken }, '*');
        if (this.isEdit) {
            this.sendQuestionnaire();
        }
    }


}