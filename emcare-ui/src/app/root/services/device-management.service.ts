import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
    providedIn: 'root'
})
export class DeviceManagementService {

    backendURL = `${environment.apiUrl}/api/device`;

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

    getAllDevices() {
        return this.http.get(`${this.backendURL}/all`, this.getHeaders());
    }

    updateDeviceStatusById(deviceId, status) {
        return this.http.get(`${this.backendURL}/status/${deviceId}/${status}`, this.getHeaders());
    }

    updateDeviceById(obj) {
        return this.http.put(`${this.backendURL}/update`, obj, this.getHeaders());
    }

    getDevicesByPageIndex(pageIndex, search?) {
        let url;
        if (search) {
            url = `${this.backendURL}/page?pageNo=${pageIndex}&orderBy=null&order=null&search=${search}`;
        } else {
            url = `${this.backendURL}/page?pageNo=${pageIndex}&orderBy=null&order=null`;
        }
        return this.http.get(url, this.getHeaders());
    }
}
