import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";

@Injectable({
    providedIn: 'root'
})
export class FhirService {

    fhirBaseURL = `${environment.apiUrl}/api/emcare`;
    deduplicationBaseURL = `${environment.apiUrl}/api/deduplication`;
    fhirResourceBaseURL = `${environment.apiUrl}/fhir`;

    constructor(private readonly http: HttpClient) { }

    getHeaders() {
        let authToken = localStorage.getItem("access_token");
        authToken = authToken && authToken.substring(1, authToken.length - 1);
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
        return this.http.get(`${this.fhirBaseURL}/patient`, this.getHeaders());
    }

    getPatientById(id: String) {
        return this.http.get(`${this.fhirBaseURL}/patient/${id}`, this.getHeaders());
    }

    getPatientByLocationId(id) {
        return this.http.get(`${this.fhirBaseURL}/patient/locationId/${id}`, this.getHeaders());
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
        return this.http.get(`${this.fhirBaseURL}/questionnaire`, this.getHeaders());
    }

    getQuestionnaireById(id: String) {
        return this.http.get(`${this.fhirBaseURL}/questionnaire/${id}`, this.getHeaders());
    }

    getQuestionnaireResourceById(id: String) {
        return this.http.get(`${this.fhirResourceBaseURL}/Questionnaire/${id}`, this.getHeaders());
    }

    getQuestionnairesByPageIndex(pageIndex) {
        return this.http.get(`${this.fhirBaseURL}/questionnaire/page?pageNo=${pageIndex}`, this.getHeaders());
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

    addFacility(data) {
        const url = `${environment.apiUrl}/fhir/Location`;
        return this.http.post(url, data, this.getHeaders());
    }

    editFacility(data, id) {
        const url = `${environment.apiUrl}/fhir/Location/${id}`;
        return this.http.put(url, data, this.getHeaders());
    }

    getFacility() {
        const url = `${environment.apiUrl}/api/emcare/active/facility`;
        return this.http.get(url, this.getHeaders());
    }

    deleteFacility(id) {
        const url = `${environment.apiUrl}/fhir/Location/${id}`;
        return this.http.delete(url, this.getHeaders());
    }

    getFacilityById(id) {
        const url = `${environment.apiUrl}/fhir/Location/${id}`;
        return this.http.get(url, this.getHeaders());
    }

    addOrganization(data) {
        const url = `${environment.apiUrl}/fhir/Organization`;
        return this.http.post(url, data, this.getHeaders());
    }

    updateOrganization(data, id) {
        const url = `${environment.apiUrl}/fhir/Organization/${id}`;
        return this.http.put(url, data, this.getHeaders());
    }

    getOrganizationById(id) {
        const url = `${environment.apiUrl}/fhir/Organization/${id}`;
        return this.http.get(url, this.getHeaders());
    }

    deleteOrganization(id) {
        const url = `${environment.apiUrl}/fhir/Organization/${id}`;
        return this.http.delete(url, this.getHeaders());
    }

    getOrganizationByPageIndexAndSearch(pageIndex, search?) {
        let url;
        if (search) {
            url = `${environment.apiUrl}/api/emcare/organization?pageNo=${pageIndex}&search=${search}`;
        } else {
            url = `${environment.apiUrl}/api/emcare/organization?pageNo=${pageIndex}`;
        }
        return this.http.get(url, this.getHeaders());
    }

    getAllAdminSettings() {
        const url = `${environment.apiUrl}/api/admin/setting`;
        return this.http.get(url, this.getHeaders());
    }

    updateSetting(body) {
        const url = `${environment.apiUrl}/api/admin/update`;
        return this.http.put(url, body, this.getHeaders());
    }

    getAllEmailTemplates() {
        const url = `${environment.apiUrl}/api/admin/mail/template`;
        return this.http.get(url, this.getHeaders());
    }

    getDashboardData() {
        const url = `${environment.apiUrl}/api/dashboard`;
        return this.http.get(url, this.getHeaders());
    }

    getAllOrganizations() {
        const url = `${environment.apiUrl}/fhir/Organization`;
        return this.http.get(url, this.getHeaders());
    }

    getFacilityByPageAndSearch(pageIndex, search?) {
        let url;
        if (search) {
            url = `${environment.apiUrl}/api/emcare/facility?pageNo=${pageIndex}&search=${search}`;
        } else {
            url = `${environment.apiUrl}/api/emcare/facility?pageNo=${pageIndex}`;
        }
        return this.http.get(url, this.getHeaders());
    }

    getChartData() {
        const url = `${environment.apiUrl}/api/dashboard/chart`;
        return this.http.get(url, this.getHeaders());
    }
}