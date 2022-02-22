import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";

@Injectable({
    providedIn:'root'
})
export class FhirService {

    fhirBaseURL= `${environment.apiUrl}/api/emcare`;
    deduplicationBaseURL = `${environment.apiUrl}/api/deduplication`;
    fhirResourceBaseURL = `${environment.apiUrl}/fhir`;

    constructor(private http: HttpClient){ }

    getHeaders() {
        let authToken = localStorage.getItem("access_token");
        authToken = authToken && authToken.substring(1,authToken.length - 1);
        const headerObj = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
                'Authorization': `Bearer ${authToken}`
            })
        };
        return headerObj;
    }

    //Endpoints
    getAllPatients() {
        return this.http.get(`${this.fhirBaseURL}/patient`,this.getHeaders());
    }

    getPatientById(id: String) {
        return this.http.get(`${this.fhirBaseURL}/patient/${id}`,this.getHeaders());
    }

    getPatientByLocationId(id) {
        return this.http.get(`${this.fhirBaseURL}/patient/locationId/${id}`,this.getHeaders());
    }

    getPatientsByPageIndex(pageIndex, search?) {
        let url;
        if (search) {
            url = `${this.fhirBaseURL}/patient/page?pageNo=${pageIndex}&search=${search}`;
        } else {
            url = `${this.fhirBaseURL}/patient/page?pageNo=${pageIndex}`;
        }
        return this.http.get(url, this.getHeaders());
    }

    //Questionnaire Endpoints
    getAllQuestionnaires() {
        return this.http.get(`${this.fhirBaseURL}/questionnaire`,this.getHeaders());
    }

    getQuestionnaireById(id: String) {
        return this.http.get(`${this.fhirBaseURL}/questionnaire/${id}`,this.getHeaders());
    }

    getQuestionnaireResourceById(id: String) {
        return this.http.get(`${this.fhirResourceBaseURL}/Questionnaire/${id}`,this.getHeaders());
    }

    getQuestionnairesByPageIndex(pageIndex) {
        return this.http.get(`${this.fhirBaseURL}/questionnaire/page?pageNo=${pageIndex}`,this.getHeaders());
    }
    getPatientsByLocationAndPageIndex(locationId, pageIndex) {
        return this.http.get(`${this.fhirBaseURL}/patient/locationId/${locationId}?pageNo=${pageIndex}`, this.getHeaders());
    }

    addLaunguage(data) {
        const url = `${environment.apiUrl}/api/language/add`;
        return this.http.post(url, data, this.getHeaders());
    }

    getAllLaunguagesTranslations() {
        const url = `${environment.apiUrl}/api/language/all`;
        return this.http.get(url, this.getHeaders());
    }

    getAllLaunguages() {
        const url = `${environment.apiUrl}/api/language/availableLanguage`;
        return this.http.get(url, this.getHeaders());
    }

    addNewLaunguage(data) {
        const url = `${environment.apiUrl}/api/language/create`;
        return this.http.post(url, data, this.getHeaders());
    }

    updateTranslation(data) {
        const url = `${environment.apiUrl}/api/language/add`;
        return this.http.post(url, data, this.getHeaders());
    }

    comparePatients(data) {
        const url = `${this.deduplicationBaseURL}/compare`;
        return this.http.post(url, data, this.getHeaders());
    }
}