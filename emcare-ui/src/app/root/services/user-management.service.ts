import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  userURL = `${environment.apiUrl}/api/user`;

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

  // User Endpoints

  createUser(user) {
    return this.http.post(`${this.userURL}/add`, user, this.getHeaders());
  }

  getAllUsers() {
    return this.http.get(`${this.userURL}/all`, this.getHeaders());
  }

  getAllSignedUpUsers() {
    return this.http.get(`${this.userURL}/signedup`, this.getHeaders())
  }

  getUserById(id) {
    return this.http.get(`${this.userURL}/${id}`, this.getHeaders());
  }

  updateUser(user, id) {
    return this.http.put(`${this.userURL}/update/${id}`, user, this.getHeaders());
  }

  updateUserStatus(user) {
    return this.http.post(`${this.userURL}/status/change`, user, this.getHeaders());
  }

  getUserByLocationId(id) {
    return this.http.get(`${this.userURL}/locationId/${id}`, this.getHeaders());
  }

  getUsersByPage(pageIndex, search?) {
    let url;
    if (search) {
      url = `${this.userURL}/page?pageNo=${pageIndex}&search=${search}`;
    } else {
      url = `${this.userURL}/page?pageNo=${pageIndex}`;
    }
    return this.http.get(url, this.getHeaders());
  }

  updatePassword(user, id) {
    return this.http.put(`${this.userURL}/update/password/${id}`, user, this.getHeaders());
  }

  getUsersByLocationAndPageIndex(locationId, pageIndex) {
    return this.http.get(`${this.userURL}/locationId/${locationId}?pageNo=${pageIndex}`, this.getHeaders());
  }
}
