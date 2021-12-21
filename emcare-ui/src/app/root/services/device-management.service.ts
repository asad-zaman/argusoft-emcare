import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class DeviceManagementService {

    backendURL = `http://localhost:8080/api/device`;

    constructor(private http: HttpClient) { }

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

    getAllDevices() {
        return this.http.get(`${this.backendURL}/all`, this.getHeaders());
    }

    updateDeviceById(obj) {
        return this.http.put(`${this.backendURL}/update`, obj, this.getHeaders());
    }
}
