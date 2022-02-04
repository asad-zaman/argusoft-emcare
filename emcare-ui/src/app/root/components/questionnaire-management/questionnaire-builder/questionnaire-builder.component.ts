import { Component, HostListener, OnInit } from "@angular/core";
import { DomSanitizer } from "@angular/platform-browser";
import { ActivatedRoute } from "@angular/router";
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
    
    constructor(
        private readonly sanitizer: DomSanitizer,
        private readonly route: ActivatedRoute, 
        private readonly fhirService: FhirService,
        private readonly toasterService: ToasterService
    ) {};

    ngOnInit(): void{
        console.log(this.questionnaireBuilderUrl);
        this.questionnaireBuilderUrl= this.sanitizer.bypassSecurityTrustResourceUrl(environment.questionnaireBuilderUrl);
        this.questionnaireBuilderEditUrl= this.sanitizer.bypassSecurityTrustResourceUrl(environment.questionnaireBuilderUrl + '/?isEdit=true');
        this.prerequisite();
    }

    //For Toasters on iframe api calls.
    @HostListener('window:message', ['$event']) onPostMessage(e) {
        if (e.data?.apiMessage == 'save') {
            this.toasterService.showSuccess('Questionnaire Saved Successfully!', 'EMCARE');
        }
        else if (e.data?.apiMessage == 'failure'){
            this.toasterService.showError('Error encountered while saving questionnaire!', 'EMCARE');
        }
    }

    prerequisite() {
        const routeParams = this.route.snapshot.paramMap;
        this.questionnaireId = routeParams.get('id');
        if(this.questionnaireId != null){
            this.isEdit = true;
        }
    }

    sendQuestionnaire() {
        let iframe = document.getElementById('iframe');
        let iWindow = (<HTMLIFrameElement>iframe).contentWindow;
        this.fhirService.getQuestionnaireResourceById(this.questionnaireId).subscribe(res => {
            iWindow.postMessage({questionnaireBody: JSON.stringify(res)}, '*');
        })
    }


    sendAuthToken() {
        let iframe = document.getElementById('iframe');
        let iWindow = (<HTMLIFrameElement>iframe).contentWindow;
        let accessToken = localStorage.getItem('access_token');
        iWindow.postMessage({accessToken: accessToken},'*');
        if(this.isEdit){
            this.sendQuestionnaire();
        }
    }
 
   
}