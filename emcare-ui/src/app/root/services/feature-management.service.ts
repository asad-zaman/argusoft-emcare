import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FeatureManagementService {

  featureURL = `${environment.apiUrl}/api/menu`;

  constructor(private readonly http: HttpClient) { }

  getHeaders() {
    let authToken = localStorage.getItem("access_token");
    authToken = authToken.substring(1, authToken.length - 1);
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

  // Feature Endpoints

  addFeatureConfig(featureConfig) {
    return this.http.post(`${this.featureURL}/config/add`, featureConfig, this.getHeaders());
  }

  getAllFeatures() {
    return this.http.get(`${this.featureURL}/all`, this.getHeaders());
  }

  getFeatureConfigById(id) {
    return this.http.get(`${this.featureURL}/menuconfig/${id}`, this.getHeaders())
  }

  deleteFeatureConfig(id) {
    return this.http.delete(`${this.featureURL}/config/delete/${id}`, this.getHeaders());
  }

  updateFeatureConfig(data) {
    return this.http.put(`${this.featureURL}/config/update`, data, this.getHeaders())
  }
}
