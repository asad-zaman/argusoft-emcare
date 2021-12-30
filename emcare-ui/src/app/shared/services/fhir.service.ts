import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";

@Injectable({
    providedIn:'root'
})
export class FhirService {

    fhirBaseURL= `${environment.apiUrl}/api/emcare`

    constructor(private http: HttpClient){ }

    getHeaders() {
        let authToken = localStorage.getItem("access_token");
        authToken = authToken.substring(1,authToken.length - 1);
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
}