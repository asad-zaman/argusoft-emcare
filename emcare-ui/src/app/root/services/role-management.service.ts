import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";

@Injectable({
    providedIn: 'root'
})
export class RoleManagementService {

    roleBaseURL = `${environment.apiUrl}/api/role`

    constructor(private http: HttpClient) { }

    getHeaders() {
        let authToken = localStorage.getItem("access_token");
        authToken = authToken ? authToken.substring(1, authToken.length - 1) : '';
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

    //Endpoints TODO

    getAllRoles() {
        return this.http.get(`${this.roleBaseURL}`, this.getHeaders());
    }

    getRoleById(id: String) {
        return this.http.get(`${this.roleBaseURL}/${id}`, this.getHeaders());
    }

    createRole(obj) {
        return this.http.post(`${this.roleBaseURL}/add`, obj, this.getHeaders());
    }

    updateRole(obj) {
        return this.http.put(`${this.roleBaseURL}/update`, obj, this.getHeaders());
    }

}