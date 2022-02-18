import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  locationURL = `${environment.apiUrl}/api/location`;

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

  // Location Type Endpoints

  createLocationType(obj) {
    return this.http.post(`${this.locationURL}/hierarchy/create`, obj, this.getHeaders());
  }

  updateLocationTypeById(obj) {
    return this.http.put(`${this.locationURL}/hierarchy/update`, obj, this.getHeaders());
  }

  getAllLocationTypes() {
    return this.http.get(`${this.locationURL}/hierarchy`, this.getHeaders());
  }

  getLocationTypeById(data) {
    return this.http.get(`${this.locationURL}/hierarchy/${data}`, this.getHeaders());
  }

  deleteLocationTypeById(data) {
    return this.http.delete(`${this.locationURL}/hierarchy/delete/${data}`, this.getHeaders());
  }

  // Location Endpoints

  createLocation(obj) {
    return this.http.post(`${this.locationURL}/create`, obj, this.getHeaders());
  }

  getAllLocations() {
    return this.http.get(`${this.locationURL}`, this.getHeaders());
  }

  getLocationById(id) {
    return this.http.get(`${this.locationURL}/${id}`, this.getHeaders());
  }

  updateLocationById(obj) {
    return this.http.put(`${this.locationURL}/update`, obj, this.getHeaders());
  }

  deleteLocationById(id) {
    return this.http.delete(`${this.locationURL}?locationId=${id}`, this.getHeaders());
  }

  getAllLocationByType(type) {
    return this.http.get(`${this.locationURL}/type/${type}`, this.getHeaders());
  }

  getChildLocationById(id) {
    return this.http.get(`${this.locationURL}/child/${id}`, this.getHeaders());
  }

  getLocationsByPageIndex(pageIndex, search?) {
    let url;
    if (search) {
      url = `${this.locationURL}/page?pageNo=${pageIndex}&orderBy=null&order=null&search=${search}`;
    } else {
      url = `${this.locationURL}/page?pageNo=${pageIndex}&orderBy=null&order=null`;
    }
    return this.http.get(url, this.getHeaders());
  }
}
